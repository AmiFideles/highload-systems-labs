package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
@EqualsAndHashCode
@ToString
public class ListingResponseDto {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    @Valid
    private List<@Valid CategoryResponseDto> categories = new ArrayList<>();

    private Long creatorId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    private Boolean used;

    public ListingResponseDto id(Long id) {
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

    public ListingResponseDto title(String title) {
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

    public ListingResponseDto description(String description) {
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

    public ListingResponseDto price(BigDecimal price) {
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

    public ListingResponseDto categories(List<@Valid CategoryResponseDto> categories) {
        this.categories = categories;
        return this;
    }

    @Valid
    @JsonProperty("categories")
    public List<@Valid CategoryResponseDto> getCategories() {
        return categories;
    }

    public void setCategories(List<@Valid CategoryResponseDto> categories) {
        this.categories = categories;
    }

    public ListingResponseDto creatorId(Long creatorId) {
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

    public ListingResponseDto createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Valid
    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ListingResponseDto updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Valid
    @JsonProperty("updated_at")
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ListingResponseDto used(Boolean used) {
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

