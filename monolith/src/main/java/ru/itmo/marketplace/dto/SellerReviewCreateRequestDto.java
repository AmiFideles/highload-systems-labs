package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class SellerReviewCreateRequestDto {

    private Long sellerId;

    private Integer rating;

    private String comment;

    public SellerReviewCreateRequestDto sellerId(Long sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    @NotNull
    @JsonProperty("seller_id")
    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public SellerReviewCreateRequestDto rating(Integer rating) {
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

    public SellerReviewCreateRequestDto comment(String comment) {
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

}

