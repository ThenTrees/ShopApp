package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.thentrees.shopapp.dtos.requests.product.ProductDTORequest;
import com.thentrees.shopapp.dtos.responses.product.ProductDTOResponse;
import com.thentrees.shopapp.models.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductDTORequest request);

    ProductDTOResponse toProductDTOResponse(Product product);

    //    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget Product product, ProductDTORequest request);
}
