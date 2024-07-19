package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.thentrees.shopapp.dtos.requests.order.OrderDtoRequest;
import com.thentrees.shopapp.dtos.responses.order.OrderDTOResponse;
import com.thentrees.shopapp.models.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toOrder(OrderDtoRequest request);

    @Mappings({@Mapping(target = "id", source = "user.id"), @Mapping(target = "orderDetails", source = "orderDetails")})
    OrderDTOResponse toOrderDTOResponse(Order order);
}
