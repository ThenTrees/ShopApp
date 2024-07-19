package com.thentrees.shopapp.controllers;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.authentication.LoginDTORequest;
import com.thentrees.shopapp.dtos.requests.authentication.RefreshTokenDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserDTORequest;
import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.dtos.responses.authentication.UserLoginDTOResponse;
import com.thentrees.shopapp.mappers.UserMapper;
import com.thentrees.shopapp.models.Token;
import com.thentrees.shopapp.models.User;
import com.thentrees.shopapp.services.token.ITokenService;
import com.thentrees.shopapp.services.user.IUserService;
import com.thentrees.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/authentications")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
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

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> login(
            @Valid @RequestBody LoginDTORequest loginDto, HttpServletRequest request) throws Exception {
        //            String token = userService.login(loginDto.getPhoneNumber(), loginDto.getPassword(),
        // loginDto.getRoleId());
        String userAgent = request.getHeader("User-Agent");
        String token = userService.login(loginDto);
        User user = userService.getUserDetailsFromToken(token);

        Token jwtToken = tokenService.addToken(user, token, isMobile(userAgent));

        UserLoginDTOResponse userLoginDTOResponse = UserLoginDTOResponse.builder()
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();

        return ResponseEntity.ok(ResponseObject.builder()
                .message(localizationUtils.getLocalizationMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .code(HttpStatus.OK.value())
                .data(userLoginDTOResponse)
                .build());
    }

    private boolean isMobile(String userAgent) {
        return userAgent.contains("mobile");
    }

    @PostMapping("/refresh-token")
    public ResponseObject refreshToken(@RequestBody RefreshTokenDTORequest request) throws Exception {

        User user = userService.getUserDetailsFromRefreshToken(request.getRefreshToken());
        Token jwtToken = tokenService.refreshToken(request.getRefreshToken(), user);

        UserLoginDTOResponse userLoginDTOResponse = UserLoginDTOResponse.builder()
                .token(jwtToken.getToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
        return ResponseObject.builder()
                .data(userLoginDTOResponse)
                .build();
    }
}
