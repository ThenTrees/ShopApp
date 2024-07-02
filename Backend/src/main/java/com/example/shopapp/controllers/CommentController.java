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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = (User) authentication.getPrincipal();
        if (userLogin.getId() != commentDTORequest.getUserId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseObject.builder()
                            .message(localizationUtils.getLocalizationMessage(MessageKeys.COMMENT_FAIL))
                            .build());
        }
        CommentDTOResponse commentDTOResponse = commentService.insertComment(commentDTORequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizationMessage(MessageKeys.INSERT_COMMENT_SUCCESSFULLY))
                        .data(commentDTOResponse)
                        .code(HttpStatus.CREATED.value())
                        .build());
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateComment(
            @PathVariable Long commentId, @RequestBody CommentUpdateDTORequest commentUpdateDTORequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = (User) authentication.getPrincipal();
        CommentDTOResponse commentDTOResponse =
                commentService.updateComment(commentId, userLogin.getId(), commentUpdateDTORequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizationMessage(MessageKeys.UPDATE_COMMENT_SUCCESSFULLY))
                        .data(commentDTOResponse)
                        .code(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteComment(@PathVariable Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userLogin = (User) authentication.getPrincipal();

        boolean rs = commentService.deleteComment(commentId, userLogin);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseObject.builder()
                        .message(
                                rs
                                        ? localizationUtils.getLocalizationMessage(
                                                MessageKeys.DELETE_COMMENT_SUCCESSFULLY)
                                        : localizationUtils.getLocalizationMessage(MessageKeys.DELETE_COMMENT_FAIL))
                        .code(HttpStatus.OK.value())
                        .build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseObject> getCommentsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get comments by product successfully")
                .data(commentService.getCommentsByProduct(productId))
                .code(HttpStatus.OK.value())
                .build());
    }

    @GetMapping("/product")
    public ResponseEntity<ResponseObject> getCommentsByProduct(
            @RequestParam() Long productId, @RequestParam() Long userId) {

        List<CommentDTOResponse> comments = commentService.getCommentsByUserAndProduct(userId, productId);

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Get comments by product successfully")
                .data(comments)
                .code(HttpStatus.OK.value())
                .build());
    }
}
