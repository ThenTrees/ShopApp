package com.example.shopapp.services.coupon;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.dtos.requests.coupon.CouponDTORequest;
import com.example.shopapp.dtos.responses.coupon.CouponDTOResponse;
import com.example.shopapp.mappers.CouponMapper;
import com.example.shopapp.models.Coupon;
import com.example.shopapp.models.CouponCondition;
import com.example.shopapp.repositories.CouponConditionRepository;
import com.example.shopapp.repositories.CouponRepository;

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

    @Override
    @Transactional
    public CouponDTOResponse createCoupon(CouponDTORequest couponDTORequest) throws RuntimeException {

        Coupon existingCoupon =
                couponRepository.findByCode(couponDTORequest.getCode()).orElse(null);
        if (existingCoupon != null) {
            throw new RuntimeException("Coupon already exists");
        }

        if (couponDTORequest.getStartDate().isAfter(couponDTORequest.getEndDate())) {
            throw new RuntimeException("Start date must be before end date");
        }

        if (couponDTORequest.getQuantity() < 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        if (couponDTORequest.getQuantityUsed() < 0) {
            throw new RuntimeException("Quantity used must be greater than 0");
        }

        if (couponDTORequest.getQuantityUsed() > couponDTORequest.getQuantity()) {
            throw new RuntimeException("Quantity used must be less than quantity");
        }

        if (couponDTORequest.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Start date must be after current date");
        }
        Coupon coupon = couponMapper.toCoupon(couponDTORequest);
        couponRepository.save(coupon);
        return couponMapper.fromCoupon(coupon);
    }

    @Override
    public List<CouponDTOResponse> getAllCoupons() {
        return couponRepository.findAll().stream().map(couponMapper::fromCoupon).toList();
    }

    @Override
    public double calculateDiscount(String couponCode, double totalAmount) {
        Coupon coupon = couponRepository.findByCode(couponCode).orElse(null);
        if (coupon == null) {
            throw new RuntimeException("Coupon not found");
        }
        if (!coupon.isActive()) {
            throw new RuntimeException("Coupon is not active");
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

            double amountDiscount = Double.valueOf(String.valueOf(condition.getDiscountAmount()));

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
            throw new RuntimeException("Coupon not found");
        }
        coupon.setActive(false);
    }
}
