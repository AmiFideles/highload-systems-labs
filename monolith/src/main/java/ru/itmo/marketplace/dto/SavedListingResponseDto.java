package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class SavedListingResponseDto {

  private ListingResponseDto listing;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime createdAt;

  public SavedListingResponseDto listing(ListingResponseDto listing) {
    this.listing = listing;
    return this;
  }

  @NotNull @Valid
  @JsonProperty("listing")
  public ListingResponseDto getListing() {
    return listing;
  }

  public void setListing(ListingResponseDto listing) {
    this.listing = listing;
  }

  public SavedListingResponseDto createdAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  @NotNull @Valid
  @JsonProperty("created_at")
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

}

