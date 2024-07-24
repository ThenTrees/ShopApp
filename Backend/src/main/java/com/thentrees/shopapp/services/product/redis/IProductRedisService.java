package com.thentrees.shopapp.services.product.redis;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thentrees.shopapp.dtos.responses.product.ProductDTOResponse;

public interface IProductRedisService {
    // xoa cache trong redis
    void clear();

    //    List<ProductDTOResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);
    List<ProductDTOResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageable)
            throws JsonProcessingException;

    ProductDTOResponse getDetailProduct(Long id) throws JsonProcessingException;

    void saveDetailProduct(ProductDTOResponse productDTOResponse) throws JsonProcessingException;

    void saveAllProducts(
            List<ProductDTOResponse> productDTOResponses, String keyword, Long categoryId, PageRequest pageable)
            throws JsonProcessingException;
}
