package com.thentrees.shopapp.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "tokens")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Token extends BaseEntity {

    @Column(name = "token", length = 255)
    String token;

    @Column(name = "refresh_token", length = 255)
    String refreshToken;

    @Column(name = "token_type", length = 50)
    String tokenType;

    @Column(name = "expiration_date")
    LocalDateTime expirationDate;

    @Column(name = "refresh_expiration_date")
    LocalDateTime refreshExpirationDate;

    @Column(name = "is_mobile", columnDefinition = "TINYINT(1)")
    boolean isMobile;

    boolean revoked;
    boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
