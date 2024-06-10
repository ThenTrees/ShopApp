package com.example.shopapp.dtos.responses;

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
public class OrderDetailDtoResponse {
    Long id;

    @JsonProperty("order_id")
    Long orderId;

    @JsonProperty("product_id")
    Long productId;

    @JsonProperty("price")
    Float price;

    @JsonProperty("number_of_products")
    int numberOfProducts;

    @JsonProperty("total_money")
    Float totalMoney;
}
