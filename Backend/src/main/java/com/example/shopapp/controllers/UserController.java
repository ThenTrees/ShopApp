package com.example.shopapp.controllers;

import java.util.List;

import com.example.shopapp.dtos.responses.user.UserDtoResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.UserDtoRequest;
import com.example.shopapp.dtos.requests.UserLoginDtoRequest;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.dtos.responses.user.UserLoginDtoResponse;
import com.example.shopapp.mappers.UserMapper;
import com.example.shopapp.models.User;
import com.example.shopapp.services.user.IUserService;
import com.example.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {
    IUserService userService;
    UserMapper userMapper;
    LocalizationUtils localizationUtils;

    @PostMapping("/register")
    // can we register an "admin" user ?
    public ResponseEntity<ResponseObject> createUser(@Valid @RequestBody UserDtoRequest request, BindingResult result)
            throws Exception {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .message(errorMessages.toString())
                            .build());
        }

        if (!request.getPassword().equals(request.getRetypePassword())) {
            // registerResponse.setMessage();
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .message("error")
                            .build());
        }
        User user = userService.createUser(request);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(userMapper.toUserDtoResponse(user))
                .message("Đăng ký tài khoản thành công")
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginDtoResponse> login(@Valid @RequestBody UserLoginDtoRequest loginDto) {
        try {
            String token = userService.login(loginDto.getPhoneNumber(), loginDto.getPassword(), loginDto.getRoleId());
            return ResponseEntity.ok(UserLoginDtoResponse.builder()
                    .message(localizationUtils.getLocalizationMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(UserLoginDtoResponse.builder()
                            .message(localizationUtils.getLocalizationMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                            .build());
        }
    }

    @PostMapping("/details")
    public ResponseEntity<ResponseObject> getMyDetailInfo(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        try {
            String extractedToken = authorizationHeader.substring(7); // bo "Bearer " ra
            UserDtoResponse userDtoResponse = userService.getMyDetailInfo(extractedToken);
            return ResponseEntity.ok(ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .data(userDtoResponse)
                    .message("success")
                    .build());
        }catch (Exception e){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ResponseObject.builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .message(localizationUtils.getLocalizationMessage(MessageKeys.LOGIN_FAILED, e.getMessage()))
                                .build());
        }
    }
}
