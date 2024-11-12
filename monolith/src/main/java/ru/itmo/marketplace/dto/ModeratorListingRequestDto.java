package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

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
public class ModeratorListingRequestDto {

    private ListingStatusDto status;

    public ModeratorListingRequestDto status(ListingStatusDto status) {
        this.status = status;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("status")
    public ListingStatusDto getStatus() {
        return status;
    }

    public void setStatus(ListingStatusDto status) {
        this.status = status;
    }

}

