package com.thentrees.shopapp.services.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thentrees.shopapp.dtos.requests.authentication.LoginDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserChangePasswordDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserUpdateDTORequest;
import com.thentrees.shopapp.dtos.responses.PageResponse;
import com.thentrees.shopapp.dtos.responses.user.UserDTOResponse;
import com.thentrees.shopapp.models.User;

public interface IUserService {
    User createUser(UserDTORequest request) throws Exception;

    String login(LoginDTORequest loginDTORequest) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String token) throws Exception;

    User updateUser(Long userId, UserUpdateDTORequest updatedUserDTO) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;

    PageResponse<?> findAllUser(int pageNo, int pageSize, String sortBy, String keyword);

    void resetPassword(Long userId, String newPassword);

    public void blockOrEnable(Long userId, Boolean active);

    UserDTOResponse getMyDetailInfo(String phone) throws Exception;

    void changePassword(User userId, UserChangePasswordDTORequest request) throws Exception;
}
