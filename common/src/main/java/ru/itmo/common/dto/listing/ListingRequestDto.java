package ru.itmo.common.dto.listing;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingRequestDto {

    @NotNull
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @NotNull
    @Valid
    @JsonProperty("price")
    private BigDecimal price;

    @Valid
    @NotNull
    @JsonProperty("category_ids")
    private List<Long> categoryIds = new ArrayList<>();

    @NotNull
    @JsonProperty("used")
    private Boolean used;

    @JsonProperty("status")
    private ListingStatusDto listingStatusDto;

    @JsonProperty("image_file_name")
    private String imageFileName;

}

