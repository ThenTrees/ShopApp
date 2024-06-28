package com.example.shopapp.dtos.responses.comment;

import com.example.shopapp.dtos.responses.BaseResponse;
import com.example.shopapp.dtos.responses.user.UserDTOResponse;
import com.example.shopapp.mappers.UserMapper;
import com.example.shopapp.models.Comment;
import com.fasterxml.jackson.annotation.JsonProperty;

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
