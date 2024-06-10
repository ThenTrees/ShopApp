package com.example.shopapp.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.shopapp.dtos.requests.OrderDetailDtoRequest;
import com.example.shopapp.dtos.responses.OrderDetailDtoResponse;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.mappers.OrderDetailMapper;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.services.orderdetail.IOrderDetailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/order-details")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderDetailController {

    IOrderDetailService orderDetailService;
    OrderDetailMapper orderDetailMapper;

    @PostMapping
    public ResponseEntity<ResponseObject> createOrderDetail(@Valid @RequestBody OrderDetailDtoRequest request)
            throws Exception {
        OrderDetail newOrderDetail = orderDetailService.createOrderDetail(request);
        OrderDetailDtoResponse orderDetailResponse = orderDetailMapper.toOrderDetailDtoResponse(newOrderDetail);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Create order detail successfully")
                        .status(HttpStatus.CREATED)
                        .data(orderDetailResponse)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@Valid @PathVariable("id") Long id) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        OrderDetailDtoResponse orderDetailResponse = orderDetailMapper.toOrderDetailDtoResponse(orderDetail);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Get order detail successfully")
                        .status(HttpStatus.OK)
                        .data(orderDetailResponse)
                        .build());
    }
    // lấy ra danh sách các order_details của 1 order nào đó theo id
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ResponseObject> getOrderDetails(@Valid @PathVariable("orderId") Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailDtoResponse> orderDetailResponses = orderDetails.stream()
                .map(orderDetailMapper::toOrderDetailDtoResponse)
                .toList();
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .message("Get order details by orderId successfully")
                        .status(HttpStatus.OK)
                        .data(orderDetailResponses)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateOrderDetail(
            @Valid @PathVariable("id") Long id, @RequestBody OrderDetailDtoRequest request)
            throws DataNotFoundException, Exception {
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, request);
        return ResponseEntity.ok()
                .body(ResponseObject.builder()
                        .data(orderDetail)
                        .message("Update order detail successfully")
                        .status(HttpStatus.OK)
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteOrderDetail(@Valid @PathVariable("id") Long id) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok()
                .body(ResponseObject.builder().message("Delete successfully").build());
    }
}
