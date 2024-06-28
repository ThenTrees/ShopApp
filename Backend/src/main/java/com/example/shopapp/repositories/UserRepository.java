package com.example.shopapp.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.shopapp.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u from User u where u.active = true AND (:keyword IS NULL OR :keyword = '' OR "
            + "u.fullName LIKE %:keyword% "
            + "OR u.phoneNumber LIKE %:keyword% "
            + "OR u.address LIKE %:keyword%) "
            + "AND LOWER(u.role.name) = 'user' ")
    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);
}
