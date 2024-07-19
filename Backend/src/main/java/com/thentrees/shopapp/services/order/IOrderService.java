package com.thentrees.shopapp.services.order;

import java.util.List;

import org.springframework.data.domain.Page;

import com.thentrees.shopapp.dtos.requests.order.OrderDtoRequest;
import com.thentrees.shopapp.models.Order;

public interface IOrderService {
    Order createOrder(OrderDtoRequest OrderRequest);

    Order getOrder(Long id);

    Order updateOrder(Long id, OrderDtoRequest OrderRequest);

    void deleteOrder(Long id);

    List<Order> findByUserId(Long userId);

    Page<Order> getOrdersByKeyword(String keyword, int pageNo, int pageSize, String sortBy);
}
