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
public class DealReviewRequestDto {

    private Integer rating;

    private String comment;

    private Long dealId;

    public DealReviewRequestDto rating(Integer rating) {
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

    public DealReviewRequestDto comment(String comment) {
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

    public DealReviewRequestDto dealId(Long dealId) {
        this.dealId = dealId;
        return this;
    }

    @NotNull
    @JsonProperty("deal_id")
    public Long getDealId() {
        return dealId;
    }

    public void setDealId(Long dealId) {
        this.dealId = dealId;
    }

}

