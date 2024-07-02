package com.example.shopapp.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.shopapp.dtos.requests.coupon.CouponDTORequest;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.services.coupon.ICouponService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CouponController {
    ICouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createCoupon(@RequestBody CouponDTORequest couponDTORequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .message("Create coupon successfully")
                        .data(couponService.createCoupon(couponDTORequest))
                        .code(HttpStatus.CREATED.value())
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Delete coupon successfully")
                        .code(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getCoupon(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Get coupon successfully")
                        .data(couponService.getCouponById(id))
                        .code(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAllCoupons() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Get all coupons successfully")
                        .data(couponService.getAllCoupons())
                        .code(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/calculate")
    public ResponseEntity<ResponseObject> calculateCoupon(
            @RequestParam("couponCode") String couponCode, @RequestParam("totalAmount") double totalAmount) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message("Calculate coupon successfully")
                        .data(couponService.calculateDiscount(couponCode, totalAmount))
                        .code(HttpStatus.OK.value())
                        .build());
    }
}
