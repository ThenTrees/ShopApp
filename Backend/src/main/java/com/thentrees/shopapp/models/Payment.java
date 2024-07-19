package com.thentrees.shopapp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "payments")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseEntity{
    String vnpCommand;
    double vnpAmount;
    String vnpBankCode;
    String vnpOrderInfo;
    String vnpOrderType;
    String vnpTxnRef;
    String status;
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    Order order;
}
