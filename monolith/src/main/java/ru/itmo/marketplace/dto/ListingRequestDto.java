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
public class ListingRequestDto {

    private String title;

    private String description;

    private BigDecimal price;

    @Valid
    private List<Long> categoryIds = new ArrayList<>();

    private Boolean used;

    public ListingRequestDto title(String title) {
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

    public ListingRequestDto description(String description) {
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

    public ListingRequestDto price(BigDecimal price) {
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

    public ListingRequestDto categoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
        return this;
    }

    @NotNull
    @JsonProperty("category_ids")
    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public ListingRequestDto used(Boolean used) {
        this.used = used;
        return this;
    }

    @NotNull
    @JsonProperty("used")
    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

}

