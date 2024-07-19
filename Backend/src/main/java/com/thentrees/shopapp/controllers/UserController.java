package com.thentrees.shopapp.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.user.UserChangePasswordDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserUpdateDTORequest;
import com.thentrees.shopapp.dtos.responses.PageResponse;
import com.thentrees.shopapp.dtos.responses.ResponseObject;
import com.thentrees.shopapp.dtos.responses.user.UserDTOResponse;
import com.thentrees.shopapp.mappers.UserMapper;
import com.thentrees.shopapp.models.User;
import com.thentrees.shopapp.services.token.ITokenService;
import com.thentrees.shopapp.services.user.IUserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    IUserService userService;
    UserMapper userMapper;
    ITokenService tokenService;
    LocalizationUtils localizationUtils;

    @PostMapping("/details")
    public ResponseEntity<ResponseObject> getMyDetailInfo() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        String phone = user.getPhoneNumber();
        UserDTOResponse userDTOResponse = userService.getMyDetailInfo(phone);
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.OK.value())
                .data(userDTOResponse)
                .message("success")
                .build());
    }

    @PutMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> updateUser(@RequestBody UserUpdateDTORequest request
            //                                                     @RequestHeader("Authorization") String
            // authorizationHeader
            ) throws Exception {
        //        String extractedToken = authorizationHeader.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        User userResponse = userService.updateUser(userId, request);
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.OK.value())
                .data(userMapper.toUserDTOResponse(userResponse))
                .message("Cập nhật thông tin tài khoản thành công")
                .build());
    }

    @PutMapping("block/{id}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> blockUser(@PathVariable Long id, @PathVariable int active) {
        userService.blockOrEnable(id, active > 0);
        String message = active > 0 ? "Successfull unblock user" : "Successfull block user";
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build());
    }

    @PutMapping("reset-password/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> resetPassword(@PathVariable Long id) {

        String newPassword = UUID.randomUUID().toString().substring(0, 5);
        userService.resetPassword(id, newPassword);
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.OK.value())
                .message("Successfull reset password")
                .data(newPassword)
                .build());
    }

    @PutMapping("change-password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody UserChangePasswordDTORequest request)
            throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        userService.changePassword(user, request);
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.OK.value())
                .message("Successfull change password")
                .build());
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id:asc") String sortBy) {
        try {
            PageResponse<?> usersPage = userService.findAllUser(page, limit, sortBy, keyword);
            return ResponseEntity.ok(usersPage);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e.getCause());
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }
}
