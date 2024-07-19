package com.thentrees.shopapp.models;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference
    Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    User user;

    String content;
}
