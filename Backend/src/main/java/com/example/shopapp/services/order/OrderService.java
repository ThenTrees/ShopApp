package com.example.shopapp.services.order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.shopapp.dtos.responses.order.OrderDTOResponse;
import com.example.shopapp.repositories.*;
import com.example.shopapp.services.coupon.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.order.CartItemDTORequest;
import com.example.shopapp.dtos.requests.order.OrderDtoRequest;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.mappers.OrderMapper;
import com.example.shopapp.models.*;
import com.example.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {
    OrderRepository orderRepository;
    UserRepository userRepository;
    OrderMapper orderMapper;
    ProductRepository productRepository;
    LocalizationUtils localizationUtils;
    OrderDetailRepository orderDetailRepository;
    CouponRepository couponRepository;
    CouponService couponService;
    @Override
    @Transactional
    public Order createOrder(OrderDtoRequest orderRequest) throws Exception {
        User user = userRepository
                .findById(orderRequest.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderRequest.getUserId()));

        Order order = orderMapper.toOrder(orderRequest);
        order.setUser(user);
        order.setOrderDate(LocalDate.now()); // lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);
        // Kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate =
                orderRequest.getShippingDate() == null ? LocalDate.now() : orderRequest.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);

        Coupon coupon = couponRepository.findByCode(orderRequest.getCouponCode()).orElse(null);
        double totalMoney = 0.0;

        List<OrderDetail> orderDetails = new ArrayList<>();
        // tạo danh sách đối tự order-detail từ danh sách cart item
        for (CartItemDTORequest cartItemDto : orderRequest.getCartItems()) {
            // tạo order detail từ cart item
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            // lấy thông tin sản phẩm từ cart item
            Product product = productRepository
                    .findById(cartItemDto.getProductId())
                    .orElseThrow(() ->
                            new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

            int quantity = cartItemDto.getQuantity();

            // đặt thông tin cho order detail
            orderDetail.setProduct(product);
            //            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setNumber_of_products(quantity);
            orderDetail.setPrice(product.getPrice());
            //            orderDetail.setTotalMoney(product.getPrice() * quantity);
            orderDetail.setTotal_money(product.getPrice() * quantity);
            totalMoney+=product.getPrice() * quantity;
            // lưu order detail
            orderDetails.add(orderDetail);
        }
        if(coupon != null) {
             totalMoney = couponService.calculateDiscount(coupon.getCode(), totalMoney);
            order.setTotalMoney(totalMoney);
        } else {
            order.setTotalMoney(totalMoney);
        }
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDtoRequest orderDTORequest) throws DataNotFoundException {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));
        User existingUser = userRepository
                .findById(orderDTORequest.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        orderMapper.toOrder(orderDTORequest);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
}
