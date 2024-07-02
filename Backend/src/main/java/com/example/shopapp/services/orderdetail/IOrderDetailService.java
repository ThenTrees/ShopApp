package com.example.shopapp.services.orderdetail;

import java.util.List;

import com.example.shopapp.dtos.requests.order.OrderDetailDTORequest;
import com.example.shopapp.models.OrderDetail;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTORequest request) throws Exception;

    OrderDetail getOrderDetail(Long id);

    OrderDetail updateOrderDetail(Long id, OrderDetailDTORequest request) throws Exception;

    void deleteById(Long id);

    List<OrderDetail> findByOrderId(Long orderId);
}
