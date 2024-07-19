package com.thentrees.shopapp.services.coupon.condition;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.coupon.CouponConditionDTORequest;
import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import com.thentrees.shopapp.models.Coupon;
import com.thentrees.shopapp.models.CouponCondition;
import com.thentrees.shopapp.repositories.CouponConditionRepository;
import com.thentrees.shopapp.repositories.CouponRepository;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponConditionService implements ICouponConditionService {
    CouponConditionRepository couponConditionRepository;
    CouponRepository couponRepository;
    LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public CouponCondition createCouponCondition(CouponConditionDTORequest coupon) throws ResourceNotFoundException {
        Coupon coupon1 = couponRepository
                .findById(coupon.getCoupon_id())
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        CouponCondition couponCondition = CouponCondition.builder()
                .coupon(coupon1)
                .attribute(coupon.getAttribute())
                .operator(coupon.getOperator())
                .value(coupon.getValue())
                .discountAmount(BigDecimal.valueOf(coupon.getDiscountAmount()))
                .build();

        couponConditionRepository.save(couponCondition);
        return couponCondition;
    }
}
