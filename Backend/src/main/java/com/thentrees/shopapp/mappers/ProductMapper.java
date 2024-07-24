package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.thentrees.shopapp.dtos.requests.product.ProductDTORequest;
import com.thentrees.shopapp.dtos.responses.product.ProductDTOResponse;
import com.thentrees.shopapp.models.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "name", source = "name"),
        @Mapping(target = "price", source = "price"),
        @Mapping(target = "description", source = "description"),
        @Mapping(target = "thumbnail", source = "thumbnail"),
        @Mapping(target = "category.id", source = "categoryId"),
        @Mapping(target = "productImages", ignore = true),
        @Mapping(target = "comments", ignore = true),
    })
    Product toProduct(ProductDTORequest request);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "price", source = "price"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "thumbnail", source = "thumbnail"),
            @Mapping(source = "category.id", target = "categoryId"),
            @Mapping(target = "productImages", ignore = true),
            @Mapping(target = "totalPages", ignore = true),
    })

    ProductDTOResponse toProductDTOResponse(Product product);

    //    @Mapping(target = "roles", ignore = true)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "price", source = "price"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "thumbnail", source = "thumbnail"),
            @Mapping(target = "category.id", source = "categoryId"),
            @Mapping(target = "productImages", ignore = true),
            @Mapping(target = "comments", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "createdAt", ignore = true),

    })
    void updateUser(@MappingTarget Product product, ProductDTORequest request);
}
