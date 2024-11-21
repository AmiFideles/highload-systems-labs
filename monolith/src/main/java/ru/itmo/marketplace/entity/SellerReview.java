package ru.itmo.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@IdClass(SellerReview.SellerReviewId.class)
@Table(name = "seller_review")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerReview {
    @Id
    private Long authorId;
    @Id
    private Long sellerId;

    @ManyToOne
    @MapsId("authorId")
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @MapsId("sellerId")
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment")
    private String comment;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SellerReviewId implements Serializable {
        @Column(name = "author_id")
        Long authorId;
        @Column(name = "seller_id")
        Long sellerId;
    }
}
