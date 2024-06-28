package com.example.shopapp.services.token;

import com.example.shopapp.models.Token;
import com.example.shopapp.models.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobile);

    Token refreshToken(String refreshToken, User user);
}
