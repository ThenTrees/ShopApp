package com.example.shopapp.services.product.redis;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.shopapp.dtos.responses.product.ProductDTOResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public void saveAllProducts(
            List<ProductDTOResponse> productDTOResponses, String keyword, Long categoryId, PageRequest pageRequest)
            throws JsonProcessingException {
        String key = this.getKeyFrom(keyword, categoryId, pageRequest);
        String json = redisObjectMapper.writeValueAsString(productDTOResponses);
        redisTemplate.opsForValue().set(key, json);
    }

    public String getKeyFrom(String keyword, Long categoryId, PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        Sort sort = pageRequest.getSort();
        String sortDirection = sort.getOrderFor("id").getDirection() == Sort.Direction.ASC ? "ASC" : "DESC";
        String key =
                String.format("all_products:%s:%d:%d:%d:%s", keyword, categoryId, pageNumber, pageSize, sortDirection);
        return key;
    }
}
