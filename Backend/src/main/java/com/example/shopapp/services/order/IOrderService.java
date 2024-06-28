package com.example.shopapp.services.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.shopapp.dtos.requests.order.OrderDtoRequest;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Order;

public interface IOrderService {
    Order createOrder(OrderDtoRequest OrderRequest) throws Exception;

    Order getOrder(Long id);

    Order updateOrder(Long id, OrderDtoRequest OrderRequest) throws DataNotFoundException;

    void deleteOrder(Long id);

    List<Order> findByUserId(Long userId);

    Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}
