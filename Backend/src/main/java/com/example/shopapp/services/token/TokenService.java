package com.example.shopapp.services.token;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.components.JwtTokenUtils;
import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.models.Token;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.TokenRepository;
import com.example.shopapp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TokenService implements ITokenService {
    private final JwtTokenUtils jwtTokenUtils;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.expiration-refreshToken}")
    private int expirationRefreshToken;

    private static final int MAX_TOKENS = 3;

    final UserRepository userRepository;
    final TokenRepository tokenRepository;
    final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Token addToken(User user, String token, boolean isMobile) {
        List<Token> userTokens = tokenRepository.findByUserId(user.getId());
        int tokenCount = userTokens.size();
        // Số lượng token vượt quá giới hạn, xóa một token cũ

        if (tokenCount >= MAX_TOKENS) {
            // kiểm tra xem trong danh sách userTokens có tồn tại ít nhất
            // một token không phải là thiết bị di động (non-mobile)
            boolean hasNoneMobileToken =
                    !userTokens.stream().allMatch(Token::isMobile); // cả 3 đều là mobile -> true <=> false
            Token tokenToDelete;
            if (hasNoneMobileToken) {
                // xoá token không phải là mobile
                tokenToDelete = userTokens.stream()
                        .filter(tokenMobile -> !tokenMobile.isMobile())
                        .findFirst()
                        .orElse(userTokens.get(0));
            } else {
                // cả 3 token đều là mobile
                tokenToDelete = userTokens.get(0);
            }
            tokenRepository.delete(tokenToDelete);
        }
        long expirationInSecond = expiration;
        LocalDateTime expirationDateTimes = LocalDateTime.now().plusSeconds(expirationInSecond);
        Token newToken = Token.builder()
                .user(user)
                .token(token)
                .expirationDate(expirationDateTimes)
                .revoked(false)
                .expired(false)
                .isMobile(isMobile)
                .build();
        newToken.setRefreshToken(jwtTokenUtils.generateRefreshToken(user));
        newToken.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(newToken);
        return newToken;
    }

    @Override
    public Token refreshToken(String refreshToken, User user) {
        Token tokenEntity = tokenRepository.findByRefreshToken(refreshToken);

        if (tokenEntity == null) {
            throw new RuntimeException(localizationUtils.getLocalizationMessage("token.invalid"));
        }

        if (tokenEntity.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(tokenEntity);
            throw new RuntimeException(localizationUtils.getLocalizationMessage("token.expired"));
        }

        String newAccessToken = jwtTokenUtils.generateToken(user);
        String newRefreshToken = jwtTokenUtils.generateRefreshToken(user);
        tokenEntity.setToken(newAccessToken);
        tokenEntity.setRefreshToken(newRefreshToken);
        tokenEntity.setExpirationDate(LocalDateTime.now().plusSeconds(expiration));
        tokenEntity.setRefreshExpirationDate(LocalDateTime.now().plusSeconds(expirationRefreshToken));
        tokenRepository.save(tokenEntity);
        return tokenEntity;
    }
}
