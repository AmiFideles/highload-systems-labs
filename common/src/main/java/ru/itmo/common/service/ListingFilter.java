package ru.itmo.common.service;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingFilter {
    private Double maxPrice;
    private Double minPrice;
    private List<Long> categoriesIn;
    private Boolean isUsed;
}
