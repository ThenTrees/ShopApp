package com.thentrees.shopapp.models;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "coupon_conditions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponCondition extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    Coupon coupon;

    @Column(name = "attribute", nullable = false)
    String attribute;

    @Column(name = "operator", nullable = false)
    String operator;

    @Column(name = "value", nullable = false)
    String value;

    @Column(name = "discount_amount", nullable = false)
    BigDecimal discountAmount;
}
/*
INSERT INTO coupons(id, code) VALUES (1, 'HEAVEN');
INSERT INTO coupons(id, code) VALUES (2, 'DISCOUNT20');

INSERT INTO coupon_conditions (id, coupon_id, attribute, operator, value, discount_amount)
VALUES (1, 1, 'minimum_amount', '>', '100', 10);

INSERT INTO coupon_conditions (id, coupon_id, attribute, operator, value, discount_amount)
VALUES (2, 1, 'applicable_date', 'BETWEEN', '2023-12-25', 5);

INSERT INTO coupon_conditions (id, coupon_id, attribute, operator, value, discount_amount)
VALUES (3, 2, 'minimum_amount', '>', '200', 20);

Nếu đơn hàng có tổng giá trị là 120 đô la và áp dụng coupon 1
Giá trị sau khi áp dụng giảm giá 10%:
120 đô la * (1 - 10/100) = 120 đô la * 0.9 = 108 đô la

Giá trị sau khi áp dụng giảm giá 5%:
108 đô la * (1 - 5/100) = 108 đô la * 0.95 = 102.6 đô la
* */
