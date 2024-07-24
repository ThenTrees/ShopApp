package com.thentrees.shopapp.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;

    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    //    @PrePersist
    //    protected void onCreate() {
    //        createdAt = LocalDateTime.now();
    //        updatedAt = LocalDateTime.now();
    //    }
    //
    //    @PreUpdate
    //    protected void onUpdate() {
    //        updatedAt = LocalDateTime.now();
    //    }
}
