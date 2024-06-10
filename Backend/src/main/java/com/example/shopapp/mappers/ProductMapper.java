package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.shopapp.dtos.requests.ProductDtoRequest;
import com.example.shopapp.dtos.responses.product.ProductDtoResponse;
import com.example.shopapp.models.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductDtoRequest request);

    ProductDtoResponse toProductDtoResponse(Product product);

    //    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget Product product, ProductDtoRequest request);
}
