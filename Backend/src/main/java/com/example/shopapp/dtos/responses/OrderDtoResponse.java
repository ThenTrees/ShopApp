package com.example.shopapp.dtos.responses;

import java.time.LocalDate;
import java.util.List;

import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderDtoResponse {
    Long id;

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("fullname")
    String fullName;

    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonProperty("email")
    String email;

    @JsonProperty("address")
    String address;

    @JsonProperty("note")
    String note;

    @JsonProperty("order_date")
    // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    LocalDate orderDate;

    @JsonProperty("status")
    String status;

    @JsonProperty("total_money")
    double totalMoney;

    @JsonProperty("shipping_method")
    String shippingMethod;

    @JsonProperty("shipping_address")
    String shippingAddress;

    @JsonProperty("shipping_date")
    LocalDate shippingDate;

    @JsonProperty("payment_method")
    String paymentMethod;

    @JsonProperty("order_details")
        List<OrderDetail> orderDetails;
//    List<OrderDetailDtoResponse> orderDetailsDtoResponse;

        public static OrderDtoResponse fromOrder(Order order) {
            OrderDtoResponse orderResponse = OrderDtoResponse.builder()
                    .id(order.getId())
                    .userId(order.getUser().getId())
                    .fullName(order.getFullName())
                    .phoneNumber(order.getPhoneNumber())
                    .email(order.getEmail())
                    .address(order.getAddress())
                    .note(order.getNote())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus())
                    .totalMoney(order.getTotalMoney())
                    .shippingMethod(order.getShippingMethod())
                    .shippingAddress(order.getShippingAddress())
                    .shippingDate(order.getShippingDate())
                    .paymentMethod(order.getPaymentMethod())
                    .orderDetails(order.getOrderDetails())
                    .build();
            return orderResponse;
        }
}
