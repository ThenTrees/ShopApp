package com.thentrees.shopapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.thentrees.shopapp.models.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(@Param("productId") Long productId);

    List<Comment> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
