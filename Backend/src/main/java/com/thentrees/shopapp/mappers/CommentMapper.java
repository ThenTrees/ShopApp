package com.thentrees.shopapp.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.thentrees.shopapp.dtos.requests.comment.CommentDTORequest;
import com.thentrees.shopapp.dtos.responses.comment.CommentDTOResponse;
import com.thentrees.shopapp.models.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "content", source = "content"),
            @Mapping(target = "user.id", source = "userId"),
            @Mapping(target = "product.id", source = "productId")
    })
    Comment toComment(CommentDTORequest commentDTORequest);

    @Mappings({@Mapping(target = "userResponse", source = "user")})
    CommentDTOResponse fromComment(Comment comment);
}
