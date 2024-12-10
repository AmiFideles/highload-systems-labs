package ru.itmo.service.market.service.impl;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;
import ru.itmo.service.market.service.ListingFilter;

import java.math.BigDecimal;
import java.util.List;

public class ListingSpecification {

    public static Specification<Listing> createListingSpecification(ListingFilter filter) {
        Specification<Listing> specification = Specification.where(null);

        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            specification = specification.and(hasPriceBetween(filter.getMinPrice(), filter.getMaxPrice()));
        }
        if (filter.getCategoriesIn() != null && !filter.getCategoriesIn().isEmpty()) {
            specification = specification.and(hasCategoryIn(filter.getCategoriesIn()));
        }
        if (filter.getIsUsed() != null) {
            specification = specification.and(isUsed(filter.getIsUsed()));
        }

        specification = specification.and(hasStatusOpen());

        return specification;
    }

    public static Specification<Listing> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice));
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(minPrice));
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), BigDecimal.valueOf(maxPrice));
            }
        };
    }

    public static Specification<Listing>    hasCategoryIn(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            // Use a subquery to verify that each listing contains all specified categories
            return categoryIds.stream()
                    .map(categoryId -> {
                        var categoryJoin = root.join("categories", JoinType.INNER);
                        return criteriaBuilder.equal(categoryJoin.get("id"), categoryId);
                    })
                    .reduce(criteriaBuilder::and)
                    .orElse(criteriaBuilder.conjunction());
        };
    }

    public static Specification<Listing> isUsed(Boolean used) {
        return (root, query, criteriaBuilder) -> {
            if (used == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("used"), used);
        };
    }

    public static Specification<Listing> hasStatusOpen() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), ListingStatus.OPEN);
    }
}
