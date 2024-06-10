package com.example.shopapp.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.shopapp.dtos.requests.OrderDtoRequest;
import com.example.shopapp.dtos.responses.OrderDtoResponse;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.mappers.OrderMapper;
import com.example.shopapp.models.Order;
import com.example.shopapp.services.order.IOrderService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/orders")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderController {
    IOrderService orderService;
    OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<ResponseObject> createOrder(@Valid @RequestBody OrderDtoRequest request, BindingResult result)
            throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(String.join(";", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        Order orderResponse = orderService.createOrder(request);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert order successfully")
                .data(orderResponse)
                .status(HttpStatus.OK)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteOrder(@Valid @PathVariable Long id) {
        // xóa mềm => cập nhật trường active = false
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Xoá thành công")
                .status(HttpStatus.OK)
                .build());
    }

    /**
     * @Destination: Lấy tất cả hoá đơn của 1 user
     * @param userId
     * @return
     */
    @GetMapping("/user/{user_id}") // Thêm biến đường dẫn "user_id"
    // GET http://localhost:8088/api/v1/orders/user/4
    public ResponseEntity<ResponseObject> getOrders(@Valid @PathVariable("user_id") Long userId) {
        List<Order> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get list of orders successfully")
                .data(orders)
                .status(HttpStatus.OK)
                .build());
    }
    // GET http://localhost:8088/api/v1/orders/2
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrder(@Valid @PathVariable("id") Long orderId) {
        Order existingOrder = orderService.getOrder(orderId);

                OrderDtoResponse orderResponse = OrderDtoResponse.fromOrder(existingOrder);
//        OrderDtoResponse orderResponse = orderMapper.toOrderDtoResponse(existingOrder);

        return ResponseEntity.ok(new ResponseObject("Get order successfully", HttpStatus.OK, orderResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateOrder(
            @Valid @PathVariable long id, @Valid @RequestBody OrderDtoRequest orderDTO) throws Exception {
        Order order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(new ResponseObject("Update order successfully", HttpStatus.OK, order));
    }

    //    @GetMapping("/get-orders-by-keyword")
    //    public ResponseEntity<OrderListResponse> getOrdersByKeyword(
    //            @RequestParam(defaultValue = "", required = false) String keyword,
    //            @RequestParam(defaultValue = "0") int page,
    //            @RequestParam(defaultValue = "10") int limit
    //    ) {
    //        // Tạo Pageable từ thông tin trang và giới hạn
    //        PageRequest pageRequest = PageRequest.of(
    //                page, limit,
    //                //Sort.by("createdAt").descending()
    //                Sort.by("id").ascending()
    //        );
    //        Page<OrderResponse> orderPage = orderService
    //                .getOrdersByKeyword(keyword, pageRequest)
    //                .map(OrderResponse::fromOrder);
    //        // Lấy tổng số trang
    //        int totalPages = orderPage.getTotalPages();
    //        List<OrderResponse> orderResponses = orderPage.getContent();
    //        return ResponseEntity.ok(OrderListResponse
    //                .builder()
    //                .orders(orderResponses)
    //                .totalPages(totalPages)
    //                .build());
    //    }
}
