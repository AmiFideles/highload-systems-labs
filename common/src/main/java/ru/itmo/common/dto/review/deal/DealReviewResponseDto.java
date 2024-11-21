package ru.itmo.common.dto.review.deal;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.itmo.common.dto.deal.DealResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealReviewResponseDto {

    @NotNull
    @JsonProperty("id")
    private Long id;

    @Valid
    @JsonProperty("deal")
    private DealResponseDto deal;

    @NotNull
    @Min(1)
    @Max(5)
    @JsonProperty("rating")
    private Integer rating;

    @JsonProperty("comment")
    private String comment;

    @NotNull
    @Valid
    @JsonProperty("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

}

