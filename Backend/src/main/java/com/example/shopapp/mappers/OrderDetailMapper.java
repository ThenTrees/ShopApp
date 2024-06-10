package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.example.shopapp.dtos.requests.OrderDetailDtoRequest;
import com.example.shopapp.dtos.responses.OrderDetailDtoResponse;
import com.example.shopapp.models.OrderDetail;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {
    @Mapping(target = "id", ignore = true)
    OrderDetail toOrderDetail(OrderDetailDtoRequest request);

    @Mappings({@Mapping(target = "orderId", source = "order.id"), @Mapping(target = "productId", source = "product.id")
    })
    OrderDetailDtoResponse toOrderDetailDtoResponse(OrderDetail orderDetail);

    void updateOrderDetailFromDto(@MappingTarget OrderDetail orderDetail, OrderDetailDtoRequest request);
}
