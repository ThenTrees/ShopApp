package com.example.shopapp.dtos.requests.coupon;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponConditionDTORequest {

    @JsonProperty("coupon_id")
    Long coupon_id;

    @JsonProperty(value = "attribute", defaultValue = "minimum_amount")
    @NotBlank(message = "Attribute is required")
    String attribute;

    @JsonProperty("operator")
    @NotBlank(message = "Operator is required")
    String operator;

    @JsonProperty("value")
    @NotBlank(message = "Value is required")
    String value;

    @JsonProperty("discount_amount")
    double discountAmount;
}
