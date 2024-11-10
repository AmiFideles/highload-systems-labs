package ru.itmo.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deal_review")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealReview {
    @Id
    @Column(name = "deal_id")
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @PrimaryKeyJoinColumn(name = "deal_id", referencedColumnName = "id")
    @MapsId
    private Deal deal;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
