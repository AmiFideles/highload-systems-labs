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
@ToString
@EqualsAndHashCode
public class SellerReviewResponseDto {

    private UserResponseDto author;

    private UserResponseDto seller;

    private Integer rating;

    private String comment;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    public SellerReviewResponseDto author(UserResponseDto author) {
        this.author = author;
        return this;
    }

    @Valid
    @JsonProperty("author")
    public UserResponseDto getAuthor() {
        return author;
    }

    public void setAuthor(UserResponseDto author) {
        this.author = author;
    }

    public SellerReviewResponseDto seller(UserResponseDto seller) {
        this.seller = seller;
        return this;
    }

    @Valid
    @JsonProperty("seller")
    public UserResponseDto getSeller() {
        return seller;
    }

    public void setSeller(UserResponseDto seller) {
        this.seller = seller;
    }

    public SellerReviewResponseDto rating(Integer rating) {
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

    public SellerReviewResponseDto comment(String comment) {
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

    public SellerReviewResponseDto createdAt(LocalDateTime createdAt) {
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

