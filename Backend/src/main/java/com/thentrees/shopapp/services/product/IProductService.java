package com.thentrees.shopapp.services.product;

import java.io.IOException;
import java.util.List;

import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.thentrees.shopapp.dtos.requests.product.ProductDTORequest;
import com.thentrees.shopapp.dtos.requests.product.ProductImageDTORequest;
import com.thentrees.shopapp.dtos.responses.product.ProductDTOResponse;
import com.thentrees.shopapp.models.Product;
import com.thentrees.shopapp.models.ProductImage;

public interface IProductService {
    Product createProduct(ProductDTORequest request) throws ResourceNotFoundException;

    Product getProductById(long id) throws ResourceNotFoundException;

    Page<ProductDTOResponse> getAllProducts(String keyword, Long categoryId, Pageable pageable);
    //    PageResponse<ProductDTOResponse> getAllProducts(String keyword, Long categoryId, long pageNo, long pageSize,
    // String sortBy);

    Product updateProduct(long id, ProductDTORequest request) throws ResourceNotFoundException;

    void deleteProduct(long id);

    boolean existsByName(String name);

    ProductImage createProductImage(Long productId, ProductImageDTORequest productImageDTORequest) throws ResourceNotFoundException;

    List<Product> findProductsByIds(List<Long> productIds);

    String storeFile(MultipartFile file) throws IOException;

    void deleteFile(String filename) throws IOException;
}
