package com.example.shopapp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.services.product.ProductService;
import com.example.shopapp.services.product.image.IProductImageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/product-images")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProductImageController {
    IProductImageService productImageService;
    ProductService productService;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        ProductImage productImage = productImageService.deleteProductImage(id);
        if (productImage != null) {
            productService.deleteFile(productImage.getImageUrl());
        }
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Delete product image successfully")
                        .data(productImage)
                        .status(HttpStatus.OK)
                        .build());
    }
}
