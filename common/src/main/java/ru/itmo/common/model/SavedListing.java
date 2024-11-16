package ru.itmo.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

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
