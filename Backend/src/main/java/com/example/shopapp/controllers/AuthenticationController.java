package com.example.shopapp.controllers;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.authentication.LoginDTORequest;
import com.example.shopapp.dtos.requests.authentication.RefreshTokenDTORequest;
import com.example.shopapp.dtos.requests.user.UserDTORequest;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.dtos.responses.authentication.UserLoginDTOResponse;
import com.example.shopapp.mappers.UserMapper;
import com.example.shopapp.models.Token;
import com.example.shopapp.models.User;
import com.example.shopapp.services.token.ITokenService;
import com.example.shopapp.services.user.IUserService;
import com.example.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/authentications")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticationController {
    IUserService userService;
    UserMapper userMapper;
    ITokenService tokenService;
    LocalizationUtils localizationUtils;

    @PostMapping("/register")
    // can we register an "admin" user ?
    public ResponseEntity<ResponseObject> createUser(@Valid @RequestBody UserDTORequest request, BindingResult result)
            throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .data(null)
                            .message(errorMessages.toString())
                            .build());
        }

        if (!request.getPassword().equals(request.getRetypePassword())) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .data(null)
                            .message(localizationUtils.getLocalizationMessage(MessageKeys.PASSWORD_NOT_MATCH))
                            .build());
        }
        User user = userService.createUser(request);
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.CREATED.value())
                .data(userMapper.toUserDTOResponse(user))
                .message(localizationUtils.getLocalizationMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDTOResponse> login(
            @Valid @RequestBody LoginDTORequest loginDto, HttpServletRequest request) throws Exception {
        //            String token = userService.login(loginDto.getPhoneNumber(), loginDto.getPassword(),
        // loginDto.getRoleId());
        String userAgent = request.getHeader("User-Agent");
        String token = userService.login(loginDto.getPhoneNumber(), loginDto.getPassword(), loginDto.getRoleId());
        User user = userService.getUserDetailsFromToken(token);

        Token jwtToken = tokenService.addToken(user, token, isMobile(userAgent));
        return ResponseEntity.ok(UserLoginDTOResponse.builder()
                .message(localizationUtils.getLocalizationMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build());
    }

    private boolean isMobile(String userAgent) {
        return userAgent.contains("mobile");
    }

    @PostMapping("/refresh-token")
    public UserLoginDTOResponse refreshToken(@RequestBody RefreshTokenDTORequest request) throws Exception {

        User user = userService.getUserDetailsFromRefreshToken(request.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(request.getRefreshToken(), user);
        return UserLoginDTOResponse.builder()
                .message(localizationUtils.getLocalizationMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
    }
}
