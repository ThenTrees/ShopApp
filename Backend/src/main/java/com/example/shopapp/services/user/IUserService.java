package com.example.shopapp.services.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.shopapp.dtos.requests.UpdateUserDtoRequest;
import com.example.shopapp.dtos.requests.UserDtoRequest;
import com.example.shopapp.dtos.responses.user.UserDtoResponse;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.InvalidPasswordException;
import com.example.shopapp.models.User;

public interface IUserService {
    User createUser(UserDtoRequest request) throws Exception;

    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String token) throws Exception;

    User updateUser(Long userId, UpdateUserDtoRequest updatedUserDTO) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;

    void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException;

    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;

    UserDtoResponse getMyDetailInfo(String token) throws Exception;
}
