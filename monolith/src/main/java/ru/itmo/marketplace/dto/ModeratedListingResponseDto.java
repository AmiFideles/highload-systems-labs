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
public class ModeratedListingResponseDto {

    @NotNull
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @NotNull
    @Valid
    @JsonProperty("price")
    private BigDecimal price;

    @NotNull
    @Valid
    @JsonProperty("categories")
    private List<@Valid CategoryResponseDto> categories = new ArrayList<>();

    @NotNull
    @JsonProperty("creator_id")
    private Long creatorId;

    @NotNull
    @JsonProperty("status")
    private String status;

}
