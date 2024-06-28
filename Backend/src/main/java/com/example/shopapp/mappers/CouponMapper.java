package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shopapp.dtos.requests.coupon.CouponDTORequest;
import com.example.shopapp.dtos.responses.coupon.CouponDTOResponse;
import com.example.shopapp.models.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(source = "active", target = "isActive")
    Coupon toCoupon(CouponDTORequest coupon);

    @Mapping(source = "active", target = "isActive")
    CouponDTOResponse fromCoupon(Coupon coupon);
}
