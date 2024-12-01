package ru.itmo.marketplace.service.review.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@IdClass(SellerReviewEntity.SellerReviewId.class)
@Table(name = "seller_review")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerReviewEntity {
    @Id
    @With
    private Long authorId;

    @Id
    @With
    private Long sellerId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment")
    private String comment;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SellerReviewId implements Serializable {
        @Column(name = "author_id")
        private Long authorId;
        @Column(name = "seller_id")
        private Long sellerId;
    }
}
