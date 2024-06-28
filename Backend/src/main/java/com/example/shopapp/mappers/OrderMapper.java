package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.example.shopapp.dtos.requests.order.OrderDtoRequest;
import com.example.shopapp.dtos.responses.order.OrderDTOResponse;
import com.example.shopapp.models.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    Order toOrder(OrderDtoRequest request);

//    @Mappings({
//        @Mapping(target = "userId", source = "user.id"),
//        //        @Mapping(target = "orderDetailsDTOResponse", source = "orderDetails")
//    })
    OrderDTOResponse toOrderDTOResponse(Order order);
}
