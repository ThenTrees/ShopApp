package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.thentrees.shopapp.dtos.requests.coupon.CouponDTORequest;
import com.thentrees.shopapp.dtos.responses.coupon.CouponDTOResponse;
import com.thentrees.shopapp.models.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(source = "active", target = "isActive")
    Coupon toCoupon(CouponDTORequest coupon);

    @Mapping(source = "active", target = "isActive")
    CouponDTOResponse fromCoupon(Coupon coupon);
}
