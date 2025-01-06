package ru.itmo.common.dto.review.deal;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealReviewCreatedNotificationDto {

    @NotNull
    @Min(1)
    @Max(5)
    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

    @NotNull
    @JsonProperty("buyer_name")
    private String buyerName;

    @NotNull
    @JsonProperty("seller_id")
    private Long sellerId;

    @NotNull
    @JsonProperty("listing_title")
    private String listingTitle;

}
