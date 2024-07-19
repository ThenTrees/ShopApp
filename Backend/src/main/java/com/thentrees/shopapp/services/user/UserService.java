package com.thentrees.shopapp.services.user;

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

import com.thentrees.shopapp.components.JwtTokenUtils;
import com.thentrees.shopapp.components.LocalizationUtils;
import com.thentrees.shopapp.dtos.requests.authentication.LoginDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserChangePasswordDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserDTORequest;
import com.thentrees.shopapp.dtos.requests.user.UserUpdateDTORequest;
import com.thentrees.shopapp.dtos.responses.PageResponse;
import com.thentrees.shopapp.dtos.responses.user.UserDTOResponse;
import com.thentrees.shopapp.exceptions.InvalidDataException;
import com.thentrees.shopapp.exceptions.ResourceNotFoundException;
import com.thentrees.shopapp.mappers.UserMapper;
import com.thentrees.shopapp.models.Role;
import com.thentrees.shopapp.models.Token;
import com.thentrees.shopapp.models.User;
import com.thentrees.shopapp.repositories.RoleRepository;
import com.thentrees.shopapp.repositories.TokenRepository;
import com.thentrees.shopapp.repositories.UserRepository;
import com.thentrees.shopapp.repositories.searchHandle.UserSearchRepository;
import com.thentrees.shopapp.utils.MessageKeys;

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
    TokenRepository tokenRepository;
    UserSearchRepository userSearchRepository;

    @Override
    @Transactional
    public User createUser(UserDTORequest request) throws Exception {
        // register user
        // Kiểm tra xem số điện thoại đã tồn tại hay chưa
        String phoneNumber = request.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizationMessage(MessageKeys.PHONE_EXISTED));
        }
        Role role = roleRepository.findByName(Role.USER);
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
    public String login(LoginDTORequest loginDTORequest) throws Exception {
        Optional<User> existUser = userRepository.findByPhoneNumber(loginDTORequest.getPhoneNumber());
        if (existUser.isEmpty()) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }

        if (!existUser.get().isActive()) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }

        if (existUser.get().getFacebookAccountId() == 0 && existUser.get().getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(
                    loginDTORequest.getPassword(), existUser.get().getPassword())) {
                throw new BadCredentialsException(
                        localizationUtils.getLocalizationMessage(MessageKeys.WRONG_PHONE_PASSWORD));
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTORequest.getPhoneNumber(),
                loginDTORequest.getPassword(),
                existUser.get().getAuthorities());
        // authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);

        return jwtTokenUtil.generateToken(existUser.get());
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        String phone = jwtTokenUtil.extractPhoneNumber(token);
        User user = userRepository
                .findByPhoneNumber(phone)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        return user;
    }

    @Override
    public User getUserDetailsFromRefreshToken(String token) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(token);
        if (existingToken == null) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateDTORequest updatedUserDTO) throws Exception {

        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

        // Check if the phone number is being changed and if it already exists for another user
        String newPhoneNumber = updatedUserDTO.getPhoneNumber();
        if (!user.getPhoneNumber().equals(newPhoneNumber) && userRepository.existsByPhoneNumber(newPhoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizationMessage((MessageKeys.PHONE_EXISTED)));
        }

        // check if the current password is correct-> process update
        if (!passwordEncoder.matches(updatedUserDTO.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException(
                    localizationUtils.getLocalizationMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }

        // Update user information based on the DTO
        if (updatedUserDTO.getFullName() != null) {
            user.setFullName(updatedUserDTO.getFullName());
        }
        if (newPhoneNumber != null) {
            user.setPhoneNumber(newPhoneNumber);
        }
        if (updatedUserDTO.getAddress() != null) {
            user.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }
        if (updatedUserDTO.getFacebookAccountId() > 0) {
            user.setFacebookAccountId(updatedUserDTO.getFacebookAccountId());
        }
        if (updatedUserDTO.getGoogleAccountId() > 0) {
            user.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }

        // Update the password if it is provided in the DTO
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            if (!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new ResourceNotFoundException(
                        localizationUtils.getLocalizationMessage(MessageKeys.PASSWORD_NOT_MATCH));
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
        }
        // Save the updated user
        return userRepository.save(user);
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) throws Exception {
        return userRepository.findAll(keyword, pageable);
    }

    @Override
    public PageResponse<?> findAllUser(int pageNo, int pageSize, String sortBy, String keyword) {
        return userSearchRepository.searchUser(pageNo, pageSize, sortBy, keyword);
    }

    @Override
    public void changePassword(User user, UserChangePasswordDTORequest request) throws Exception {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidDataException(localizationUtils.getLocalizationMessage(MessageKeys.PASSWORD_NOT_MATCH));
        }
        if (!request.getNewPassword().equals(request.getRetypeNewPassword())) {
            throw new InvalidDataException(localizationUtils.getLocalizationMessage(MessageKeys.PASSWORD_NOT_MATCH));
        }
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) throws ResourceNotFoundException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    @Override
    public void blockOrEnable(Long userId, Boolean active) throws ResourceNotFoundException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));
        user.setActive(active);
        userRepository.save(user);
    }

    @Override
    public UserDTOResponse getMyDetailInfo(String phone) throws Exception {

        Optional<User> user = userRepository.findByPhoneNumber(phone);

        if (!user.get().isActive()) {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.USER_IS_LOCKED));
        }

        if (user.isPresent()) {
            return userMapper.toUserDTOResponse(user.get());
        } else {
            throw new ResourceNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }
    }
}
