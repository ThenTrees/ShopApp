package com.thentrees.shopapp.services.coupon.condition;

import com.thentrees.shopapp.dtos.requests.coupon.CouponConditionDTORequest;
import com.thentrees.shopapp.models.CouponCondition;

public interface ICouponConditionService {
    CouponCondition createCouponCondition(CouponConditionDTORequest coupon);
}
