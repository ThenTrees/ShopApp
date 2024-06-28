package com.example.shopapp.services.product.redis;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.example.shopapp.dtos.responses.product.ProductDTOResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface IProductRedisService {
    // xoa cache trong redis
    void clear();

    List<ProductDTOResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest)
            throws JsonProcessingException;

    void saveAllProducts(
            List<ProductDTOResponse> productDTOResponses, String keyword, Long categoryId, PageRequest pageRequest)
            throws JsonProcessingException;
}
