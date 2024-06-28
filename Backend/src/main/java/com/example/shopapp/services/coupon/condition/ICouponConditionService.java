package com.example.shopapp.services.coupon.condition;

import com.example.shopapp.dtos.requests.coupon.CouponConditionDTORequest;
import com.example.shopapp.models.CouponCondition;

public interface ICouponConditionService {
    CouponCondition createCouponCondition(CouponConditionDTORequest coupon);
}
