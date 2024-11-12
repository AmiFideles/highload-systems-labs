package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}

