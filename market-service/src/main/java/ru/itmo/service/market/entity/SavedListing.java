package ru.itmo.service.market.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@IdClass(SavedListing.SavedListingId.class)
@Table(name = "saved_listing")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedListing {
    @Id
    private Long userId;
    @Id
    private Long listingId;

    @ManyToOne
    @MapsId("listingId")
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class SavedListingId implements Serializable {
        @Column(name = "user_id")
        private Long userId;
        @Column(name = "listing_id")
        private Long listingId;

    }
}
