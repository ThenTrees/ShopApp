package com.thentrees.shopapp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thentrees.shopapp.dtos.requests.coupon.CouponConditionDTORequest;
import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.services.coupon.condition.ICouponConditionService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/coupon-conditions")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CouponConditionController {
    ICouponConditionService couponConditionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createCoupon(@RequestBody CouponConditionDTORequest couponDTORequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .message("Create coupon condition successfully")
                        .data(couponConditionService.createCouponCondition(couponDTORequest))
                        .code(HttpStatus.CREATED.value())
                        .build());
    }
}
