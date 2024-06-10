package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.shopapp.dtos.requests.UserDtoRequest;
import com.example.shopapp.dtos.responses.user.UserDtoResponse;
import com.example.shopapp.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserDtoRequest userDtoRequest);

    UserDtoResponse toUserDtoResponse(User user);
}
