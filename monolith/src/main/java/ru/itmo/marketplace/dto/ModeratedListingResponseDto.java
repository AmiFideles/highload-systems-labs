package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
public class ModeratedListingResponseDto {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    @Valid
    private List<@Valid CategoryResponseDto> categories = new ArrayList<>();

    private Long creatorId;

    private String status;

    public ModeratedListingResponseDto id(Long id) {
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

    public ModeratedListingResponseDto title(String title) {
        this.title = title;
        return this;
    }

    @NotNull
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ModeratedListingResponseDto description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ModeratedListingResponseDto price(BigDecimal price) {
        this.price = price;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ModeratedListingResponseDto categories(List<@Valid CategoryResponseDto> categories) {
        this.categories = categories;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("categories")
    public List<@Valid CategoryResponseDto> getCategories() {
        return categories;
    }

    public void setCategories(List<@Valid CategoryResponseDto> categories) {
        this.categories = categories;
    }

    public ModeratedListingResponseDto creatorId(Long creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    @NotNull
    @JsonProperty("creator_id")
    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public ModeratedListingResponseDto status(String status) {
        this.status = status;
        return this;
    }

    @NotNull
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

