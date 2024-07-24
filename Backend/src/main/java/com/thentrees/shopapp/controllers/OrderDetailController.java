package com.thentrees.shopapp.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.order.OrderDetailDTORequest;
import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.dtos.responses.order.OrderDetailDTOResponse;
import com.thentrees.shopapp.mappers.OrderDetailMapper;
import com.thentrees.shopapp.models.OrderDetail;
import com.thentrees.shopapp.services.orderdetail.IOrderDetailService;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/order-details")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderDetailController {

    private static final Logger log = LoggerFactory.getLogger(OrderDetailController.class);
    IOrderDetailService orderDetailService;
    OrderDetailMapper orderDetailMapper;
    LocalizationUtils localizationUtils;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOrderDetail(@Valid @RequestBody OrderDetailDTORequest request)
            throws Exception {
        OrderDetail newOrderDetail = orderDetailService.createOrderDetail(request);
        OrderDetailDTOResponse orderDetailResponse = orderDetailMapper.toOrderDetailDTOResponse(newOrderDetail);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Create order detail successfully")
                        .code(HttpStatus.CREATED.value())
                        .data(orderDetailResponse)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrderDetail(@Valid @PathVariable("id") Long id) {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        OrderDetailDTOResponse orderDetailResponse = orderDetailMapper.toOrderDetailDTOResponse(orderDetail);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Get order detail successfully")
                        .code(HttpStatus.OK.value())
                        .data(orderDetailResponse)
                        .build());
    }
    // lấy ra danh sách các order_details của 1 order nào đó theo id
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseObject> getOrderDetails(@Valid @PathVariable("orderId") Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailDTOResponse> orderDetailResponses = orderDetails.stream()
                .map(orderDetailMapper::toOrderDetailDTOResponse)
                .toList();
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Get order details by orderId successfully")
                        .code(HttpStatus.OK.value())
                        .data(orderDetailResponses)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> updateOrderDetail(
            @Valid @PathVariable("id") Long id, @RequestBody OrderDetailDTORequest request) throws Exception {
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("phoneNumber: {}", authentication.getName());
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .data(orderDetail)
                        .message("Update order detail successfully")
                        .code(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> deleteOrderDetail(@Valid @PathVariable("id") Long id) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizationMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY))
                        .code(HttpStatus.OK.value())
                        .build());
    }
}
