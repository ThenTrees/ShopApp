package com.thentrees.shopapp.services.order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thentrees.shopapp.constant.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.order.CartItemDTORequest;
import com.thentrees.shopapp.dtos.requests.order.OrderDtoRequest;
import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import com.thentrees.shopapp.mappers.OrderMapper;
import com.thentrees.shopapp.models.*;
import com.thentrees.shopapp.repositories.*;
import com.thentrees.shopapp.services.coupon.CouponService;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService implements IOrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
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
    public Order createOrder(OrderDtoRequest orderRequest) {
        User user = getUser(orderRequest.getUserId());
        Order order = orderMapper.toOrder(orderRequest);
        order.setUser(user);
        order.setOrderDate(LocalDate.now()); // lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);
        // Kiểm tra shipping date phải >= ngày hôm nay
        LocalDate shippingDate =
                orderRequest.getShippingDate() == null ? LocalDate.now() : orderRequest.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);

        Coupon coupon =
                couponRepository.findByCode(orderRequest.getCouponCode()).orElse(null);
        double totalMoney = 0;

        List<OrderDetail> orderDetails = new ArrayList<>();
        // tạo danh sách đối tự order-detail từ danh sách cart item
        for (CartItemDTORequest cartItemDto : orderRequest.getCartItems()) {
            // tạo order detail từ cart item
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            // lấy thông tin sản phẩm từ cart item
            Product product = productRepository
                    .findById(cartItemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

            int quantity = cartItemDto.getQuantity();

            // đặt thông tin cho order detail
            orderDetail.setProduct(product);
            //            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setNumber_of_products(quantity);
            orderDetail.setPrice(product.getPrice());
            //            orderDetail.setTotalMoney(product.getPrice() * quantity);
            orderDetail.setTotal_money(product.getPrice() * quantity);
            totalMoney += product.getPrice() * quantity;
            // lưu order detail
            orderDetails.add(orderDetail);
        }
        if (coupon != null) {
            totalMoney = couponService.calculateDiscount(coupon.getCode(), totalMoney);

            order.setTotalMoney((int) totalMoney);
        } else {
            order.setTotalMoney((int) totalMoney);
        }
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        log.info("Order created successfully");
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDtoRequest orderDTORequest) throws ResourceNotFoundException {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        User existingUser = getUser(orderDTORequest.getUserId());
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
    public Page<Order> getOrdersByKeyword(String keyword, int pageNo, int pageSize, String sortBy) {
        String direction = "ASC";
        if (StringUtils.hasLength(keyword)) {
            keyword = keyword.trim();
        }
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sortBy = matcher.group(1);
                direction = matcher.group(3);
            } else {
                sortBy = "id";
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(direction), sortBy));
        return orderRepository.findByKeyword(keyword, pageable);
    }

    private User getUser(Long userId) throws ResourceNotFoundException {
        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
    }
}
