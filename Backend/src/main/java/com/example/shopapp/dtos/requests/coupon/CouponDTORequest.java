package com.example.shopapp.dtos.requests.coupon;

import java.time.LocalDate;

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
public class CouponDTORequest {
    @JsonProperty("code")
    @NotBlank(message = "Code is required")
    String code;

    String description;

    @JsonProperty(value = "is_active")
    @Builder.Default
    boolean isActive = true;

    @JsonProperty(value = "type", defaultValue = "fixed")
    String type;

    @JsonProperty(value = "start_date")
    LocalDate startDate;

    @JsonProperty(value = "end_date")
    LocalDate endDate;

    // số lượng discount
    @JsonProperty(value = "quantity", defaultValue = "10")
    int quantity;

    // số lượng đã sử dụng
    @JsonProperty(value = "quantity_used", defaultValue = "0")
    int quantityUsed;
}
