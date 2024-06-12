package com.example.shopapp.services.user;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.components.JwtTokenUtils;
import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.UpdateUserDtoRequest;
import com.example.shopapp.dtos.requests.UserDtoRequest;
import com.example.shopapp.dtos.responses.user.UserDtoResponse;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.InvalidPasswordException;
import com.example.shopapp.exceptions.PermissionDenyException;
import com.example.shopapp.mappers.UserMapper;
import com.example.shopapp.models.Role;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.RoleRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.utils.MessageKeys;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    JwtTokenUtils jwtTokenUtil;
    AuthenticationManager authenticationManager;
    LocalizationUtils localizationUtils;
    JwtTokenUtils jwtTokenUtils;

    @Override
    @Transactional
    public User createUser(UserDtoRequest request) throws Exception {
        // register user
        // Kiểm tra xem số điện thoại đã tồn tại hay chưa
        String phoneNumber = request.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizationMessage(MessageKeys.PHONE_EXISTED));
        }
        Role role = roleRepository
                .findById(request.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizationMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));
        if (role.getName().toUpperCase().equals(Role.ADMIN)) {
            throw new PermissionDenyException("Không được phép đăng ký tài khoản Admin");
        }

        User newUser = userMapper.toUser(request);
        newUser.setActive(true);
        newUser.setRole(role);
        // Kiểm tra nếu có accountId, không yêu cầu password
        if (request.getFacebookAccountId() == 0 && request.getGoogleAccountId() == 0) {
            String password = request.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        Optional<User> existUser = userRepository.findByPhoneNumber(phoneNumber);
        if (existUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }

        if (!existUser.get().isActive()) {
            throw new DataNotFoundException("Account is not active");
        }

        if (existUser.get().getFacebookAccountId() == 0 && existUser.get().getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, existUser.get().getPassword())) {
                throw new BadCredentialsException(
                        localizationUtils.getLocalizationMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password, existUser.get().getAuthorities());

        // authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existUser.get());
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        return null;
    }

    @Override
    public User getUserDetailsFromRefreshToken(String token) throws Exception {
        return null;
    }

    @Override
    public User updateUser(Long userId, UpdateUserDtoRequest updatedUserDTO) throws Exception {
        return null;
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return null;
    }

    @Override
    public void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException {}

    @Override
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {}

    @Override
    public UserDtoResponse getMyDetailInfo(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpired(token)) {
            throw new Exception("Token expired");
        }

        String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);

        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (!user.get().isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.USER_IS_LOCKED));
        }

        if (user.isPresent()) {
            return userMapper.toUserDtoResponse(user.get());
        } else {
            throw new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }
    }
}
