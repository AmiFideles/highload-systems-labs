package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import jakarta.validation.Valid;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealCreateRequestDto {

    @NotNull
    @JsonProperty("listing_id")
    private Long listingId;

    @NotNull
    @Valid
    @DecimalMin("0")
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

}

