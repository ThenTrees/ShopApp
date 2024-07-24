package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.thentrees.shopapp.dtos.requests.order.OrderDtoRequest;
import com.thentrees.shopapp.dtos.responses.order.OrderDTOResponse;
import com.thentrees.shopapp.models.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "orderDetails", ignore = true),
            @Mapping(target = "user.id", source = "userId"),
            @Mapping(target = "totalMoney", source = "totalMoney"),
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "phoneNumber", source = "phoneNumber"),
            @Mapping(target = "email", source = "email"),
            @Mapping(target = "payments", ignore = true),
            @Mapping(target = "orderDate", ignore = true),
    })
    Order toOrder(OrderDtoRequest request);


    @Mappings({@Mapping(target = "id", source = "user.id"), @Mapping(target = "orderDetails", source = "orderDetails")})
    OrderDTOResponse toOrderDTOResponse(Order order);
}
