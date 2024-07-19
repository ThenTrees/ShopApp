package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.thentrees.shopapp.dtos.requests.user.UserDTORequest;
import com.thentrees.shopapp.dtos.responses.user.UserDTOResponse;
import com.thentrees.shopapp.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    //    @Mapping(target = "id", ignore = true)
    User toUser(UserDTORequest userDTORequest);

    UserDTOResponse toUserDTOResponse(User user);
}
