package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

import jakarta.validation.Valid;

import jakarta.validation.constraints.DecimalMin;
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
public class DealCreateRequestDto {

    private Long listingId;

    private BigDecimal totalPrice;

    public DealCreateRequestDto listingId(Long listingId) {
        this.listingId = listingId;
        return this;
    }

    @NotNull
    @JsonProperty("listing_id")
    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public DealCreateRequestDto totalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    @NotNull
    @Valid
    @DecimalMin("0")
    @JsonProperty("total_price")
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

}

