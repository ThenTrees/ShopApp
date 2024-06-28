package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.shopapp.dtos.requests.user.UserDTORequest;
import com.example.shopapp.dtos.responses.user.UserDTOResponse;
import com.example.shopapp.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDTORequest userDTORequest);

    UserDTOResponse toUserDTOResponse(User user);
}
