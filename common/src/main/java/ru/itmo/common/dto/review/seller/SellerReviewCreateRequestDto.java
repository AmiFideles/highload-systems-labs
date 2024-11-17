package ru.itmo.common.dto.review.seller;

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
public class SellerReviewCreateRequestDto {

    @NotNull
    @JsonProperty("seller_id")
    private Long sellerId;

    @Min(1)
    @Max(5)
    @NotNull
    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

}

