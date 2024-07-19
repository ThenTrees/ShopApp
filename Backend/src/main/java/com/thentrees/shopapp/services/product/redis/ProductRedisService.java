package com.thentrees.shopapp.services.product.redis;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thentrees.shopapp.dtos.responses.product.ProductDTOResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductRedisService implements IProductRedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper redisObjectMapper;

    @Value("${spring.data.redis.use-redis-cache}")
    private boolean useRedisCache;

    private static final Logger logger = LoggerFactory.getLogger(ProductRedisService.class);

    @Override
    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Override
    public List<ProductDTOResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest)
            throws JsonProcessingException {
        if (useRedisCache == false) {
            return null;
        }
        // tạo khoá key
        String key = getKeyFrom(keyword, categoryId, pageRequest);
        // lấy dữ liệu từ khoá
        String json = (String) redisTemplate.opsForValue().get(key);

        // chuyển dữ liệu lấy từ redis thành dto response
        List<ProductDTOResponse> productDTOResponses = json != null
                ? redisObjectMapper.readValue(json, new TypeReference<List<ProductDTOResponse>>() {})
                : null;
        return productDTOResponses;
    }

    @Override
    public ProductDTOResponse getDetailProduct(Long id) throws JsonProcessingException {
        if (useRedisCache == false) {
            return null;
        }
        String key = this.getKeyFromDetailProduct(id);
        String json = (String) redisTemplate.opsForValue().get(key);
        ProductDTOResponse productDTOResponse =
                json != null ? redisObjectMapper.readValue(json, ProductDTOResponse.class) : null;
        return productDTOResponse;
    }

    @Override
    public void saveDetailProduct(ProductDTOResponse productDTOResponse, Long id) throws JsonProcessingException {
        String key = this.getKeyFromDetailProduct(id);
        String json = redisObjectMapper.writeValueAsString(productDTOResponse);
        redisTemplate.opsForValue().set(key, json);
    }

    @Override
    public void saveAllProducts(
            List<ProductDTOResponse> productDTOResponses, String keyword, Long categoryId, PageRequest pageable)
            throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, pageable);
        String json = redisObjectMapper.writeValueAsString(productDTOResponses);
        redisTemplate.opsForValue().set(key, json);
    }

    private String getKeyFrom(String keyword, Long categoryId, PageRequest pageRequest) {
        int page = pageRequest.getPageNumber();
        int limit = pageRequest.getPageSize();
        String sortBy = pageRequest.getSort().toString();
        String field = "";
        String direction = "";
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                field = matcher.group(1);
                direction = matcher.group(3).trim();
            }
        }
        String key =
                String.format("all_products:%s:%d:%d:%d:%s:%s", keyword, categoryId, page, limit, field, direction);
        return key;
    }

    private String getKeyFromDetailProduct(Long id) {
        return String.format("product:%d", id);
    }
}
