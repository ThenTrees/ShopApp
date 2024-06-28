package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.shopapp.dtos.requests.product.ProductDTORequest;
import com.example.shopapp.dtos.responses.product.ProductDTOResponse;
import com.example.shopapp.models.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductDTORequest request);

    ProductDTOResponse toProductDTOResponse(Product product);

    //    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget Product product, ProductDTORequest request);
}
