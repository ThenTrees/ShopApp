package com.example.shopapp.services.coupon;

import java.util.List;

import com.example.shopapp.dtos.requests.coupon.CouponDTORequest;
import com.example.shopapp.dtos.responses.coupon.CouponDTOResponse;
import com.example.shopapp.models.Coupon;

public interface ICouponService {
    CouponDTOResponse createCoupon(CouponDTORequest coupon) throws RuntimeException;

    List<CouponDTOResponse> getAllCoupons();

    Coupon getCouponById(Long id);

    void deleteCoupon(Long id);

    double calculateDiscount(String couponCode, double totalAmount);
}
