package com.example.shopapp.dtos.responses.order;

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
public class OrderDetailDTOResponse {
    Long id;

    @JsonProperty("order_id")
    Long orderId;

    @JsonProperty("product_id")
    Long productId;

    @JsonProperty("price")
    Double price;

    @JsonProperty("number_of_products")
    int numberOfProducts;

    @JsonProperty("total_money")
    Double totalMoney;
}
