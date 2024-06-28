package com.example.shopapp.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "orders")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "fullname", length = 100)
    String fullName;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "phone_number", nullable = false, length = 100)
    String phoneNumber;

    @Column(name = "address", length = 100)
    String address;

    @Column(name = "note", length = 100)
    String note;

    @Column(name = "order_date")
    LocalDate orderDate;

    @Column(name = "status")
    String status;

    @Column(name = "total_money")
    Double totalMoney;

    @Column(name = "shipping_method")
    String shippingMethod; // phương thức vận chuyển

    @Column(name = "shipping_address")
    String shippingAddress; // địa chỉ giao hàng

    @Column(name = "shipping_date")
    LocalDate shippingDate; // ngày giao hàng

    @Column(name = "tracking_number")
    String trackingNumber; // mã vận đơn

    @Column(name = "payment_method")
    String paymentMethod; // phương thức thanh toán

    @Column(name = "active")
    Boolean active; // thuộc về admin

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    List<OrderDetail> orderDetails;
}
