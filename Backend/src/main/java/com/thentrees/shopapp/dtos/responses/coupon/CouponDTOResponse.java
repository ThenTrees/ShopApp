package com.thentrees.shopapp.dtos.responses.coupon;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

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
public class CouponDTOResponse {
    Long id;

    @JsonProperty("code")
    @NotBlank(message = "Code is required")
    String code;

    String description;

    @JsonProperty(value = "is_active")
    boolean isActive;

    @JsonProperty(value = "type", defaultValue = "fixed")
    String type;

    @JsonProperty(value = "start_date")
    LocalDate startDate;

    @JsonProperty(value = "end_date")
    LocalDate endDate;

    // số lượng discount
//    @JsonProperty(value = "quantity", defaultValue = "10")
//    int quantity;
    //
    // số lượng đã sử dụng
//    @JsonProperty(value = "quantity_used", defaultValue = "0")
//    int quantityUsed;
}
