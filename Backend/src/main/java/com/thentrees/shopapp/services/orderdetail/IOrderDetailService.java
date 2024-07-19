package com.thentrees.shopapp.services.orderdetail;

import java.util.List;

import com.thentrees.shopapp.dtos.requests.order.OrderDetailDTORequest;
import com.thentrees.shopapp.models.OrderDetail;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTORequest request) throws Exception;

    OrderDetail getOrderDetail(Long id);

    OrderDetail updateOrderDetail(Long id, OrderDetailDTORequest request) throws Exception;

    void deleteById(Long id);

    List<OrderDetail> findByOrderId(Long orderId);
}
