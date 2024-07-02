package com.example.shopapp.services.orderdetail;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.order.OrderDetailDTORequest;
import com.example.shopapp.exceptions.ResourceNotFoundException;
import com.example.shopapp.mappers.OrderDetailMapper;
import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.models.Product;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailService implements IOrderDetailService {

    OrderDetailRepository orderDetailRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderDetailMapper orderDetailMapper;
    LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTORequest request) throws Exception {
        // tìm xem orderId có tồn tại ko
        Order order = orderRepository
                .findById(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        // Tìm Product theo id
        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        // tạo mới orderDetail từ request và lưu vào db
        // lưu vào db
        OrderDetail od = orderDetailMapper.toOrderDetail(request);
        od.setOrder(order);
        od.setProduct(product);
        return orderDetailRepository.save(od);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) {
        return orderDetailRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTORequest request) {
        // tìm xem order detail có tồn tại ko đã
        OrderDetail existingOrderDetail = orderDetailRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        Order existingOrder = orderRepository
                .findById(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        Product existingProduct = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

        orderDetailMapper.updateOrderDetailFromDto(existingOrderDetail, request);
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
