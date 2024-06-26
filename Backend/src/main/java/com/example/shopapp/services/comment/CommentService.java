package com.example.shopapp.services.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.comment.CommentDTORequest;
import com.example.shopapp.dtos.requests.comment.CommentUpdateDTORequest;
import com.example.shopapp.dtos.responses.comment.CommentDTOResponse;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.mappers.CommentMapper;
import com.example.shopapp.models.Comment;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.CommentRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.utils.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CommentService implements ICommentService {
    CommentRepository commentRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    LocalizationUtils localizationUtils;
    CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDTOResponse insertComment(CommentDTORequest commentDTORequest) throws DataNotFoundException {

        Product product = productRepository
                .findById(commentDTORequest.getProductId())
                .orElseThrow(() ->
                        new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

        User user = userRepository
                .findById(commentDTORequest.getUserId())
                .orElseThrow(() ->
                        new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

        //        Comment comment = Comment.builder()
        //                .content(commentDTORequest.getComment())
        //                .product(product)
        //                .user(user)
        //                .build();

        Comment comment = commentMapper.toComment(commentDTORequest);
        comment.setProduct(product);
        comment.setUser(user);
        commentRepository.save(comment);
        return CommentDTOResponse.fromComment(comment);
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, User user) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);

        if (user.getRole().getName().equals("ADMIN")) {
            commentRepository.deleteById(commentId);
            return true;
        }

        if (commentOptional.isPresent()
                && commentOptional.get().getUser().getId().equals(user.getId())) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public CommentDTOResponse updateComment(
            Long commentId, Long userId, CommentUpdateDTORequest commentUpdateDTORequest) throws DataNotFoundException {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND)));

        if (!comment.getUser().getId().equals(userId)) {
            throw new DataNotFoundException(localizationUtils.getLocalizationMessage(MessageKeys.NOT_FOUND));
        }

        comment.setContent(commentUpdateDTORequest.getContent());
        commentRepository.save(comment);
        return CommentDTOResponse.fromComment(comment);
    }

    @Override
    public List<CommentDTOResponse> getCommentsByUserAndProduct(Long userId, Long productId) {
        List<Comment> comments = commentRepository.findByUserIdAndProductId(userId, productId);
        return comments.stream().map(CommentDTOResponse::fromComment).toList();
    }

    @Override
    public List<CommentDTOResponse> getCommentsByProduct(Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        return comments.stream().map(CommentDTOResponse::fromComment).toList();
    }
}
