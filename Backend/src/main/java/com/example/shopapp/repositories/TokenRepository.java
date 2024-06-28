package com.example.shopapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopapp.models.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserId(Long userId);

    Token findByToken(String token);

    Token findByRefreshToken(String refreshToken);
}
