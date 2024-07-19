package com.thentrees.shopapp.dtos.requests.order;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
public class OrderDtoRequest {
    @JsonProperty("user_id")
    @Min(value = 1, message = "User's ID must be > 0")
    Long userId;

    @JsonProperty("fullname")
    String fullName;

    String email;

    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    @Size(min = 10, message = "Phone number must be at least 10 characters")
    String phoneNumber;

    @JsonProperty("status")
    String status;

    String address;

    String note;

    @JsonProperty("shipping_method")
    String shippingMethod;

    @JsonProperty("shipping_address")
    String shippingAddress;

    @JsonProperty("shipping_date")
    LocalDate shippingDate;

    @JsonProperty("payment_method")
    String paymentMethod;

    @JsonProperty("coupon_code")
    String couponCode;

    @JsonProperty("cart_items")
    List<CartItemDTORequest> cartItems;
}
