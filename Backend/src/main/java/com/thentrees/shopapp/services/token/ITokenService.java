package com.thentrees.shopapp.services.token;

import com.thentrees.shopapp.models.Token;
import com.thentrees.shopapp.models.User;

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobile);

    Token refreshToken(String refreshToken, User user);
}
