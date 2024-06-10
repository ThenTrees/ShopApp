package com.example.shopapp.services.product;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import com.example.shopapp.dtos.requests.ProductDtoRequest;
import com.example.shopapp.dtos.requests.ProductImageDtoRequest;
import com.example.shopapp.dtos.responses.product.ProductDtoResponse;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;

public interface IProductService {
    Product createProduct(ProductDtoRequest request) throws Exception;

    Product getProductById(long id) throws Exception;

    public Page<ProductDtoResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);

    Product updateProduct(long id, ProductDtoRequest request) throws Exception;

    void deleteProduct(long id);

    boolean existsByName(String name);

    ProductImage createProductImage(Long productId, ProductImageDtoRequest productImageDtoRequest) throws Exception;

    List<Product> findProductsByIds(List<Long> productIds);

    String storeFile(MultipartFile file) throws IOException;

    void deleteFile(String filename) throws IOException;
}
