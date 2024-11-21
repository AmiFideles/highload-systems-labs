package ru.itmo.common.dto.deal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.itmo.common.dto.listing.ListingResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealResponseDto {

    @NotNull
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("buyer_id")
    private Long buyerId;

    @NotNull
    @Valid
    @JsonProperty("listing")
    private ListingResponseDto listing;

    @NotNull
    @Valid
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    @NotNull
    @Valid
    @JsonProperty("status")
    private DealStatusDto status;

    @NotNull
    @Valid
    @JsonProperty("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

}

