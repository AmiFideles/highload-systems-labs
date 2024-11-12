package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class SavedListingRequestDto {

    private Long listingId;

    public SavedListingRequestDto listingId(Long listingId) {
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

}

