package com.example.shopapp.services.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.shopapp.dtos.requests.user.UserChangePasswordDTORequest;
import com.example.shopapp.dtos.requests.user.UserDTORequest;
import com.example.shopapp.dtos.requests.user.UserUpdateDTORequest;
import com.example.shopapp.dtos.responses.user.UserDTOResponse;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.InvalidPasswordException;
import com.example.shopapp.models.User;

public interface IUserService {
    User createUser(UserDTORequest request) throws Exception;

    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String token) throws Exception;

    User updateUser(Long userId, UserUpdateDTORequest updatedUserDTO) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;

    void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException;

    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;

    UserDTOResponse getMyDetailInfo(String phone) throws Exception;

    void changePassword(User userId, UserChangePasswordDTORequest request) throws Exception;
}
