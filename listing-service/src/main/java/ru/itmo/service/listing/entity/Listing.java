package ru.itmo.service.listing.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Table("listing")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing {
    @Id
    private Long id;

    @Column("title")
    @NotNull
    private String title;

    @Column("description")
    private String description;

    @NotNull
    @Column("price")
    private BigDecimal price;

    @NotNull
    @Column("creator_id")
    private Long creatorId;

    @NotNull
    @Column("status")
    @Builder.Default
    private ListingStatus status = ListingStatus.REVIEW;

    @NotNull
    @Column("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    @Builder.Default
    @Column("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @NotNull
    @Column("used")
    boolean used;
}
