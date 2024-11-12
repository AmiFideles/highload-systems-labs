package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class DealReviewResponseDto {

    private Long id;

    private DealResponseDto deal;

    private Integer rating;

    private String comment;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    public DealReviewResponseDto id(Long id) {
        this.id = id;
        return this;
    }

    @NotNull
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DealReviewResponseDto deal(DealResponseDto deal) {
        this.deal = deal;
        return this;
    }

    @Valid
    @JsonProperty("deal")
    public DealResponseDto getDeal() {
        return deal;
    }

    public void setDeal(DealResponseDto deal) {
        this.deal = deal;
    }

    public DealReviewResponseDto rating(Integer rating) {
        this.rating = rating;
        return this;
    }

    @NotNull
    @Min(1)
    @Max(5)
    @JsonProperty("rating")
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public DealReviewResponseDto comment(String comment) {
        this.comment = comment;
        return this;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DealReviewResponseDto createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}

