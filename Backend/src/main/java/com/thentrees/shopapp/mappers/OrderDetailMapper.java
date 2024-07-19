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
    //    @Mapping(target = "id", ignore = true)
    OrderDetail toOrderDetail(OrderDetailDTORequest request);

    @Mappings({@Mapping(target = "orderId", source = "order.id"), @Mapping(target = "productId", source = "product.id")
    })
    OrderDetailDTOResponse toOrderDetailDTOResponse(OrderDetail orderDetail);

    @Mappings({
        @Mapping(source = "numberOfProducts", target = "number_of_products"),
        @Mapping(source = "totalMoney", target = "total_money"),
    })
    void updateOrderDetailFromDto(@MappingTarget OrderDetail orderDetail, OrderDetailDTORequest request);
}
