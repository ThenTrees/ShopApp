package com.example.shopapp.models;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @Column(name = "price", nullable = false)
    Double price;

    @Column(name = "number_of_products")
    //    int numberOfProducts;
    int number_of_products;

    @Column(name = "total_money")
    Double total_money;
    //    Double totalMoney;

    //    @Column(name = "color")
    //    String color;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    @JsonBackReference
    Coupon coupon;
}
