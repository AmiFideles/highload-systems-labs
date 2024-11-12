package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class DealResponseDto {

    private Long id;

    private Long buyerId;

    private ListingResponseDto listing;

    private BigDecimal totalPrice;

    private DealStatusDto status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    public DealResponseDto id(Long id) {
        this.id = id;
        return this;
    }

    @NotNull
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DealResponseDto buyerId(Long buyerId) {
        this.buyerId = buyerId;
        return this;
    }

    @NotNull
    @JsonProperty("buyer_id")
    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public DealResponseDto listing(ListingResponseDto listing) {
        this.listing = listing;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("listing")
    public ListingResponseDto getListing() {
        return listing;
    }

    public void setListing(ListingResponseDto listing) {
        this.listing = listing;
    }

    public DealResponseDto totalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("total_price")
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public DealResponseDto status(DealStatusDto status) {
        this.status = status;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("status")
    public DealStatusDto getStatus() {
        return status;
    }

    public void setStatus(DealStatusDto status) {
        this.status = status;
    }

    public DealResponseDto createdAt(LocalDateTime createdAt) {
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

