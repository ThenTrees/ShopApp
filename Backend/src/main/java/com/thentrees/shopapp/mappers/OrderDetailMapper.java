package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.thentrees.shopapp.dtos.requests.order.OrderDetailDTORequest;
import com.thentrees.shopapp.dtos.responses.order.OrderDetailDTOResponse;
import com.thentrees.shopapp.models.OrderDetail;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    @Mappings({@Mapping(target = "id", ignore = true),
        @Mapping(target = "number_of_products", source = "numberOfProducts"),
        @Mapping(target = "total_money", source = "totalMoney"),
        @Mapping(target = "order.id", source = "orderId"),
        @Mapping(target = "product.id", source = "productId"),
        @Mapping(target = "coupon.id", source = "couponId"),
    })
    OrderDetail toOrderDetail(OrderDetailDTORequest request);

    @Mappings({
        @Mapping(target = "orderId", source = "order.id"),
        @Mapping(target = "productId", source = "product.id"),
        @Mapping(target = "numberOfProducts", source = "number_of_products"),
        @Mapping(target = "totalMoney", source = "total_money")
    })
    OrderDetailDTOResponse toOrderDetailDTOResponse(OrderDetail orderDetail);

    @Mappings({
        @Mapping(source = "numberOfProducts", target = "number_of_products"),
        @Mapping(source = "totalMoney", target = "total_money"),
        @Mapping(source = "orderId", target = "order.id"),
        @Mapping(source = "productId", target = "product.id"),
        @Mapping(source = "couponId", target = "coupon.id"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),

    })
    void updateOrderDetailFromDto(@MappingTarget OrderDetail orderDetail, OrderDetailDTORequest request);
}
