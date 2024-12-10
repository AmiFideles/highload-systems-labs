package ru.itmo.service.market.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.dto.user.UserRoleDto;
import ru.itmo.service.market.entity.*;
import ru.itmo.service.market.repository.CategoryRepository;
import ru.itmo.service.market.repository.DealRepository;
import ru.itmo.service.market.repository.ListingRepository;
import ru.itmo.service.market.service.SavedListingService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class TestUtils {
    private final CategoryRepository categoryRepository;
    private final DealRepository dealRepository;
    private final ListingRepository listingRepository;
    private final SavedListingService savedListingService;

    public UserResponseDto ADMIN = UserResponseDto.builder().id(1L).role(UserRoleDto.ADMIN).build();
    public UserResponseDto MODERATOR = UserResponseDto.builder().id(2L).role(UserRoleDto.MODERATOR).build();

    public UserResponseDto BUYER = UserResponseDto.builder().id(3L).role(UserRoleDto.BUYER).build();
    public UserResponseDto SELLER = UserResponseDto.builder().id(4L).role(UserRoleDto.SELLER).build();

    public UserResponseDto BUYER2 = UserResponseDto.builder().id(5L).role(UserRoleDto.BUYER).build();
    public UserResponseDto SELLER2 = UserResponseDto.builder().id(6L).role(UserRoleDto.SELLER).build();

    public UserResponseDto BUYER3 = UserResponseDto.builder().id(7L).role(UserRoleDto.BUYER).build();
    public UserResponseDto SELLER3 = UserResponseDto.builder().id(8L).role(UserRoleDto.SELLER).build();

    public UserResponseDto BUYER4 = UserResponseDto.builder().id(9L).role(UserRoleDto.BUYER).build();
    public UserResponseDto SELLER4 = UserResponseDto.builder().id(10L).role(UserRoleDto.SELLER).build();

    public List<UserResponseDto> USERS = List.of(
            ADMIN,
            MODERATOR,
            BUYER,
            SELLER,
            BUYER2,
            SELLER2,
            BUYER3,
            SELLER3,
            BUYER4,
            SELLER4
    );

    public SavedListing createSavedListing(long userId) {
        Listing listing = createReviewListing();
        return savedListingService.create(
                SavedListing.builder()
                        .userId(userId)
                        .listingId(listing.getId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    public Category createCategory() {
        return categoryRepository.save(
                Category.builder()
                        .name(createRandomString())
                        .build()
        );
    }

    public Listing createOpenListing() {
        BigDecimal price = BigDecimal.valueOf(createRandomPositiveInt());
        return createOpenListing(price);
    }

    public Listing createOpenListing(Long sellerId) {
        BigDecimal price = BigDecimal.valueOf(createRandomPositiveInt());
        return createOpenListing(sellerId, price);
    }

    public Listing createOpenListing(BigDecimal price) {
        return createOpenListing(SELLER.getId(), price);
    }

    public Listing createOpenListing(Long sellerId, BigDecimal price) {
        return listingRepository.save(
                Listing.builder()
                        .title(createRandomString())
                        .description(createRandomString())
                        .price(price)
                        .categories(Set.of(createCategory(), createCategory(), createCategory()))
                        .creatorId(sellerId)
                        .status(ListingStatus.OPEN)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }


    public Listing createReviewListing() {
        return createReviewListing(SELLER.getId());
    }

    public Listing createReviewListing(long sellerId) {
        Listing listing = Listing.builder()
                .title(createRandomString())
                .description(createRandomString())
                .price(BigDecimal.valueOf(createRandomPositiveInt()))
                .categories(Set.of(createCategory(), createCategory(), createCategory()))
                .creatorId(sellerId)
                .status(ListingStatus.REVIEW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return listingRepository.save(listing);
    }


    public String createRandomString() {
        return UUID.randomUUID().toString();
    }

    public int createRandomInt() {
        return ThreadLocalRandom.current().nextInt(-100000, 100000 + 1);
    }

    public int createRandomPositiveInt() {
        return Math.abs(createRandomInt() + 1);
    }

    public Deal createPendingDeal() {
        return createPendingDeal(BUYER.getId());
    }

    public Deal createPendingDeal(Long buyerId) {
        return createPendingDeal(buyerId, SELLER.getId());
    }

    public Deal createCompletedDeal(Long buyerId) {
        return createDeal(buyerId, SELLER.getId(), DealStatus.COMPLETED);
    }

    public Deal createCompletedDeal(Long buyerId, Long sellerId) {
        return createDeal(buyerId, sellerId, DealStatus.COMPLETED);
    }

    public Deal createPendingDeal(Long buyerId, Long sellerId) {
        return createDeal(buyerId, sellerId, DealStatus.PENDING);
    }

    public Deal createDeal(Long buyerId, Long sellerId, DealStatus status) {
        Listing listing = createOpenListing(sellerId);
        Deal deal = dealRepository.save(Deal.builder()
                .buyerId(buyerId)
                .listing(listing)
                .totalPrice(BigDecimal.valueOf(createRandomPositiveInt()))
                .createdAt(LocalDateTime.now())
                .build());
        deal.setStatus(status);
        return dealRepository.save(deal);
    }
}
