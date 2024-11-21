package ru.itmo.marketplace.service;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListingFilter {
    private Double maxPrice;
    private Double minPrice;
    private List<Long> categoriesIn;
    private Boolean isUsed;
}
