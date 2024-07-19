package com.thentrees.shopapp.dtos.responses.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thentrees.shopapp.dtos.responses.BaseResponse;
import com.thentrees.shopapp.dtos.responses.user.UserDTOResponse;
import com.thentrees.shopapp.mappers.UserMapper;
import com.thentrees.shopapp.models.Comment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CommentDTOResponse extends BaseResponse {
    Long id;

    @JsonProperty("user")
    UserDTOResponse userResponse;

    @JsonProperty("content")
    String content;

    public static CommentDTOResponse fromComment(Comment comment) {
        UserMapper userMapper = UserMapper.INSTANCE;
        CommentDTOResponse commentResponse = CommentDTOResponse.builder()
                .id(comment.getId())
                .userResponse(userMapper.toUserDTOResponse(comment.getUser()))
                .content(comment.getContent())
                .build();
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        return commentResponse;
    }
}
