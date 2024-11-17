package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.Valid;

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

