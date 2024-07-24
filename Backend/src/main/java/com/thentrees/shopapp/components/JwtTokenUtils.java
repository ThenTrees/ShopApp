package com.thentrees.shopapp.components;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

import com.thentrees.shopapp.models.Token;
import com.thentrees.shopapp.models.User;
import com.thentrees.shopapp.repositories.TokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class JwtTokenUtils {
    private final TokenRepository tokenRepository;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refreshToken}")
    private int expirationRefreshToken;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user) {
        // casc thuoc tinh goi la claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("userId", user.getId());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // 1000 miliseconds
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Cannot create jwt token, error: {}", e.getMessage());
            return null;
        }
    }

    public String generateRefreshToken(User user) {
        // casc thuoc tinh goi la claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("userId", user.getId());
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(
                            new Date(System.currentTimeMillis() + expirationRefreshToken * 1000L)) // 1000 miliseconds
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            log.error("Cannot create jwt rf token, error: {}", e.getMessage());
            return null;
        }
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * extract cuj the claim nao do
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // check expiration
    public Boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public String extractPhoneNumber(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, User userDetails) {
        final String phoneNumber = this.extractPhoneNumber(token);
        Token existToken = tokenRepository.findByToken(token);
        if (existToken == null || existToken.isRevoked() || !userDetails.isActive()) {
            return false;
        }
        return (phoneNumber.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
