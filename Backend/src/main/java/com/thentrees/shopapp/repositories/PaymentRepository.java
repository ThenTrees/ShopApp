package com.thentrees.shopapp.repositories;

import com.thentrees.shopapp.models.Order;
import com.thentrees.shopapp.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByVnpTxnRef(String vnp_TxnRef);

    Payment findByVnpTxnRef(String vnp_TxnRef);
}
