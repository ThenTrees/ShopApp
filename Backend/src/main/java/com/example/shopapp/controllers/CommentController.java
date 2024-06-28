package com.example.shopapp.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.requests.comment.CommentDTORequest;
import com.example.shopapp.dtos.requests.comment.CommentUpdateDTORequest;
import com.example.shopapp.dtos.responses.ResponseObject;
import com.example.shopapp.dtos.responses.comment.CommentDTOResponse;
import com.example.shopapp.models.User;
import com.example.shopapp.services.comment.ICommentService;
import com.example.shopapp.utils.MessageKeys;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class CommentController {
    ICommentService commentService;
    private final LocalizationUtils localizationUtils;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createComment(@Valid @RequestBody CommentDTORequest commentDTORequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User userLogin = (User) authentication.getPrincipal();
            if (userLogin.getId() != commentDTORequest.getUserId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ResponseObject.builder()
                                .message("You are not allowed to comment on behalf of another user")
                                .build());
            }
            CommentDTOResponse commentDTOResponse = commentService.insertComment(commentDTORequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseObject.builder()
                            .message("Comment created successfully")
                            .data(commentDTOResponse)
                            .status(HttpStatus.CREATED)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder().message("Error").build());
        }
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateComment(
            @PathVariable Long commentId, @RequestBody CommentUpdateDTORequest commentUpdateDTORequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User userLogin = (User) authentication.getPrincipal();
            CommentDTOResponse commentDTOResponse =
                    commentService.updateComment(commentId, userLogin.getId(), commentUpdateDTORequest);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .message("Comment updated successfully")
                            .data(commentDTOResponse)
                            .status(HttpStatus.OK)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizationMessage(MessageKeys.NOT_COMMENT))
                            .build());
        }
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteComment(@PathVariable Long commentId) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User userLogin = (User) authentication.getPrincipal();

            boolean rs = commentService.deleteComment(commentId, userLogin);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseObject.builder()
                            .message(rs ? "Comment deleted successfully" : "Error, delete comment failed!!!")
                            .status(HttpStatus.OK)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Error, you don't policy delete other' comment!!!")
                            .build());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseObject> getCommentsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get comments by product successfully")
                .data(commentService.getCommentsByProduct(productId))
                .status(HttpStatus.OK)
                .build());
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseObject> getCommentsByProduct(
            @RequestParam() Long productId, @RequestParam() Long userId) {

        List<CommentDTOResponse> comments = commentService.getCommentsByUserAndProduct(userId, productId);

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get comments by product successfully")
                .data(comments)
                .status(HttpStatus.OK)
                .build());
    }
}
