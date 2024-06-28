package com.example.shopapp.services.comment;

import java.util.List;

import com.example.shopapp.dtos.requests.comment.CommentDTORequest;
import com.example.shopapp.dtos.requests.comment.CommentUpdateDTORequest;
import com.example.shopapp.dtos.responses.comment.CommentDTOResponse;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.User;

public interface ICommentService {
    CommentDTOResponse insertComment(CommentDTORequest commentDTORequest) throws DataNotFoundException;

    boolean deleteComment(Long commentId, User user);

    CommentDTOResponse updateComment(Long commentId, Long userId, CommentUpdateDTORequest commentUpdateDTORequest)
            throws DataNotFoundException;

    List<CommentDTOResponse> getCommentsByUserAndProduct(Long userId, Long productId);

    List<CommentDTOResponse> getCommentsByProduct(Long productId);
}
