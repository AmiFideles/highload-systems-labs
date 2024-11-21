package ru.itmo.common.dto.saved;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedListingRequestDto {

    @NotNull
    @JsonProperty("listing_id")
    private Long listingId;

}

