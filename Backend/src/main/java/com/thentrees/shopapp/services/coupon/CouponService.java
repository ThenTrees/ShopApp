package com.thentrees.shopapp.services.coupon;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.coupon.CouponDTORequest;
import com.thentrees.shopapp.dtos.responses.coupon.CouponDTOResponse;
import com.thentrees.shopapp.exceptions.InvalidDataException;
import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import com.thentrees.shopapp.mappers.CouponMapper;
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
public class CouponService implements ICouponService {

    CouponRepository couponRepository;
    CouponConditionRepository couponConditionRepository;
    CouponMapper couponMapper;
    LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public CouponDTOResponse createCoupon(CouponDTORequest couponDTORequest) throws RuntimeException {

        Coupon existingCoupon =
                couponRepository.findByCode(couponDTORequest.getCode()).orElse(null);
        if (existingCoupon != null) {
            throw new InvalidDataException(localizationUtils.getLocalizationMessage(MessageKeys.COUPON_ALREADY_EXISTS));
        }

        if (couponDTORequest.getStartDate().isAfter(couponDTORequest.getEndDate())) {
            throw new InvalidDataException(localizationUtils.getLocalizationMessage(MessageKeys.DATE_IS_BEFORE));
        }

//        if (couponDTORequest.getQuantity() < 0) {
//            throw new InvalidDataException(localizationUtils.getLocalizationMessage(MessageKeys.INVALID_QUANTITY));
//        }

        Coupon coupon = couponMapper.toCoupon(couponDTORequest);
        couponRepository.save(coupon);
        return couponMapper.fromCoupon(coupon);
    }

    @Override
    public List<CouponDTOResponse> getAllCoupons() {
        return couponRepository.findAll().stream().map(couponMapper::fromCoupon).toList();
    }

    @Override
    public double calculateDiscount(String couponCode, double totalAmount) throws RuntimeException {
        Coupon coupon = couponRepository.findByCode(couponCode).orElse(null);
        if (coupon == null) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }
        if (!coupon.isActive()) {
            throw new RuntimeException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_ACTIVE));
        }
        double discount = calculateDiscount(coupon, totalAmount);
        return totalAmount - discount;
    }

    private double calculateDiscount(Coupon coupon, double totalAmount) {
        List<CouponCondition> conditions = couponConditionRepository.findByCouponId(coupon.getId());
        double discount = 0.0;
        double updatedTotalAmount = totalAmount;
        String type = coupon.getType();
        for (CouponCondition condition : conditions) {
            // EAV(Entity - Attribute - Value) Model
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();

            double amountDiscount = Double.parseDouble(String.valueOf(condition.getDiscountAmount()));

            if (attribute.equals("minimum_amount")) {
                if ((operator.equals(">") && updatedTotalAmount > Double.parseDouble(value))) {
                    discount +=
                            type.equalsIgnoreCase("fixed") ? amountDiscount : updatedTotalAmount * amountDiscount / 100;
                }
            } else if (attribute.equals("applicable_date")) {
                LocalDate applicableDate = LocalDate.parse(value);
                LocalDate currentDate = LocalDate.now();
                if (operator.equalsIgnoreCase("BETWEEN") && currentDate.isEqual(applicableDate)) {
                    discount +=
                            type.equalsIgnoreCase("fixed") ? amountDiscount : updatedTotalAmount * amountDiscount / 100;
                }
            }
            // còn nhiều nhiều điều kiện khác nữa
            updatedTotalAmount = updatedTotalAmount - discount;
        }
        return discount;
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        if (coupon == null) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }
        coupon.setActive(false);
    }
}
