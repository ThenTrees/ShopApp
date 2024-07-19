package com.thentrees.shopapp.controllers;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.order.OrderDtoRequest;
import com.thentrees.shopapp.dtos.responses.PageResponse;
import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.dtos.responses.order.OrderDTOResponse;
import com.thentrees.shopapp.models.Order;
import com.thentrees.shopapp.services.order.IOrderService;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/orders")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OrderController {
    IOrderService orderService;
    private final LocalizationUtils localizationUtils;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> createOrder(@Valid @RequestBody OrderDtoRequest request) {
        Order order = orderService.createOrder(request);
        OrderDTOResponse orderResponse = OrderDTOResponse.fromOrder(order);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Insert order successfully")
                .data(orderResponse)
                .code(HttpStatus.CREATED.value())
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteOrder(@Valid @PathVariable Long id) {
        // xóa mềm => cập nhật trường active = false
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizationMessage(MessageKeys.DELETE_ORDER_SUCCESSFULLY))
                .code(HttpStatus.OK.value())
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
                .code(HttpStatus.OK.value())
                .build());
    }
    // GET http://localhost:8088/api/v1/orders/2
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrder(@Valid @PathVariable("id") Long orderId) {
        Order existingOrder = orderService.getOrder(orderId);
        OrderDTOResponse orderResponse = OrderDTOResponse.fromOrder(existingOrder);
        //        OrderDTOResponse orderResponse = orderMapper.toOrderDTOResponse(existingOrder);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get order successfully")
                .data(orderResponse)
                .code(HttpStatus.OK.value())
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateOrder(
            @Valid @PathVariable long id, @Valid @RequestBody OrderDtoRequest orderDTO) {
        Order order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get order successfully")
                .data(order)
                .code(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/get-orders-by-keyword")
    public ResponseEntity<PageResponse> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<Order> orderPage = orderService.getOrdersByKeyword(keyword, page, limit, sortBy);
        // Lấy tổng số trang
        int totalPages = orderPage.getTotalPages();
        List<Order> orderPageResponses = orderPage.getContent();
        List<OrderDTOResponse> orderResponses =
                orderPageResponses.stream().map(OrderDTOResponse::fromOrder).collect(Collectors.toList());

        return ResponseEntity.ok(PageResponse.builder()
                .datas(orderResponses)
                .total(totalPages)
                .page(page)
                .size(limit)
                .build());
    }
}
