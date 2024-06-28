package com.example.shopapp.dtos.requests.order;

import jakarta.validation.constraints.Min;

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
public class OrderDetailDTORequest {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's ID must be > 0")
    Long orderId;

    @Min(value = 1, message = "Product's ID must be > 0")
    @JsonProperty("product_id")
    Long productId;

    @Min(value = 0, message = "Product's ID must be >= 0")
    Double price;

    @Min(value = 1, message = "number_of_products must be >= 1")
    @JsonProperty("number_of_products")
    int numberOfProducts;

    @Min(value = 0, message = "total_money must be >= 0")
    @JsonProperty("total_money")
    Double totalMoney;
}
