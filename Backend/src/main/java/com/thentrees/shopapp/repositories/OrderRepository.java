package com.thentrees.shopapp.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thentrees.shopapp.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Tìm các đơn hàng của 1 user nào đó
    List<Order> findByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE o.active = true "
            + "AND (:keyword IS NULL OR :keyword = '' "
            + "OR o.fullName LIKE %:keyword% "
            + "OR o.address LIKE %:keyword% "
            + "OR o.note LIKE %:keyword% "
            + "OR o.email LIKE %:keyword%) ")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.payments p WHERE p.vnpTxnRef = :vnp_TxnRef")
    Order findByVnp_TxnRef(@Param("vnp_TxnRef") String vnp_TxnRef);
}
