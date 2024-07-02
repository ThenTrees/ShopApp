package com.example.shopapp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.user.UserChangePasswordDTORequest;
import com.example.shopapp.dtos.requests.user.UserUpdateDTORequest;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.dtos.responses.user.ListUserDTOResponse;
import com.example.shopapp.dtos.responses.user.UserDTOResponse;
import com.example.shopapp.mappers.UserMapper;
import com.example.shopapp.models.User;
import com.example.shopapp.services.token.ITokenService;
import com.example.shopapp.services.user.IUserService;

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
    public ResponseEntity<ResponseObject> getAllUsers(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit)
            throws Exception {
        // tạo pageable
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<UserDTOResponse> usersPage =
                userService.findAll(keyword, pageRequest).map(userMapper::toUserDTOResponse);
        // lấy tổng số trang
        int totalPage = usersPage.getTotalPages();
        // lấy tổng số phần tử
        List<UserDTOResponse> users = usersPage.getContent();
        ListUserDTOResponse listUserDTOResponse =
                ListUserDTOResponse.builder().users(users).totalPages(totalPage).build();
        return ResponseEntity.ok(ResponseObject.builder()
                .code(HttpStatus.OK.value())
                .data(listUserDTOResponse)
                .message("Successfull get all users")
                .build());
    }
}
