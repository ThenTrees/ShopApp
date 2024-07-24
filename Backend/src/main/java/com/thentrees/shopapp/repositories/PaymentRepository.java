package com.thentrees.shopapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thentrees.shopapp.models.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByVnpTxnRef(String vnp_TxnRef);

    Payment findByVnpTxnRef(String vnp_TxnRef);
}
