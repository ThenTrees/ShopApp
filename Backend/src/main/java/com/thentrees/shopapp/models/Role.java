package com.thentrees.shopapp.models;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "name", nullable = false)
    private String name;

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
}
