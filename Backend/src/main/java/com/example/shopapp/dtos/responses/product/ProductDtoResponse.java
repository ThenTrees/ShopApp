package com.example.shopapp.dtos.responses.product;

import java.util.ArrayList;
import java.util.List;

import com.example.shopapp.dtos.responses.BaseResponse;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductDtoResponse extends BaseResponse {
    Long id;
    String name;
    Float price;
    String thumbnail;
    String description;
    // Thêm trường totalPages
    //    int totalPages;

    @JsonProperty("category_id")
    Long categoryId;

    @JsonProperty("product_images")
    List<ProductImage> productImages = new ArrayList<>();

    public static ProductDtoResponse fromProduct(Product product) {
        ProductDtoResponse productResponse = ProductDtoResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .productImages(product.getProductImages())
                .build();
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        return productResponse;
    }
}
