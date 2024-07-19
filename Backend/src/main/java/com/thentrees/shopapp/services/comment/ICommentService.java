package com.thentrees.shopapp.services.comment;

import java.util.List;

import com.thentrees.shopapp.dtos.requests.comment.CommentDTORequest;
import com.thentrees.shopapp.dtos.requests.comment.CommentUpdateDTORequest;
import com.thentrees.shopapp.dtos.responses.comment.CommentDTOResponse;
import com.thentrees.shopapp.models.User;

public interface ICommentService {
    CommentDTOResponse insertComment(CommentDTORequest commentDTORequest);

    boolean deleteComment(Long commentId, User user);

    CommentDTOResponse updateComment(Long commentId, Long userId, CommentUpdateDTORequest commentUpdateDTORequest);

    List<CommentDTOResponse> getCommentsByUserAndProduct(Long userId, Long productId);

    List<CommentDTOResponse> getCommentsByProduct(Long productId);
}
