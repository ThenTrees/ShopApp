package com.example.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.shopapp.dtos.requests.comment.CommentDTORequest;
import com.example.shopapp.dtos.responses.comment.CommentDTOResponse;
import com.example.shopapp.models.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mappings({
        @Mapping(target = "product.id", source = "productId"),
        @Mapping(target = "user.id", source = "userId"),
        @Mapping(target = "content", source = "content")
    })
    Comment toComment(CommentDTORequest commentDTORequest);

    @Mappings({@Mapping(target = "userResponse", source = "user")})
    CommentDTOResponse fromComment(Comment comment);
}
