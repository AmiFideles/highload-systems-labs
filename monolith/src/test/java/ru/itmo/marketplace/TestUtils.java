package ru.itmo.marketplace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.marketplace.entity.*;
import ru.itmo.marketplace.repository.*;
import ru.itmo.marketplace.service.SavedListingService;
import ru.itmo.marketplace.service.SellerReviewService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class TestUtils {
    private final CategoryRepository categoryRepository;
    private final DealReviewRepository dealReviewRepository;
    private final DealRepository dealRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    private final SavedListingService savedListingService;
    private final SellerReviewService sellerReviewService;

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

    public Listing createOpenListing(User seller) {
        BigDecimal price = BigDecimal.valueOf(createRandomPositiveInt());
        return createOpenListing(seller, price);
    }

    public Listing createOpenListing(BigDecimal price) {
        User seller = createSeller();
        return createOpenListing(seller, price);
    }

    public Listing createOpenListing(User seller, BigDecimal price) {
        return listingRepository.save(
                Listing.builder()
                        .title(createRandomString())
                        .description(createRandomString())
                        .price(price)
                        .categories(Set.of(createCategory(), createCategory(), createCategory()))
                        .creator(seller)
                        .status(ListingStatus.OPEN)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    public Listing createReviewListing() {
        User seller = createSeller();
        return createReviewListing(seller.getId());
    }

    public Listing createReviewListing(long sellerId) {
        User seller = userRepository.findById(sellerId).get();
        Listing listing = Listing.builder()
                .title(createRandomString())
                .description(createRandomString())
                .price(BigDecimal.valueOf(createRandomPositiveInt()))
                .categories(Set.of(createCategory(), createCategory(), createCategory()))
                .creator(seller)
                .status(ListingStatus.REVIEW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return listingRepository.save(listing);
    }

    public User createSeller() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(Role.SELLER)
                .build());
    }

    public User createBuyer() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(Role.BUYER)
                .build());
    }

    public User createModerator() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(Role.MODERATOR)
                .build());
    }

    public User createAdmin() {
        return userRepository.save(User.builder()
                .email(createRandomString())
                .password(createRandomString())
                .name(createRandomString())
                .role(Role.ADMIN)
                .build());
    }

    public SellerReview createSellerReview() {
        User buyer = createBuyer();
        User seller = createSeller();
        return createSellerReview(buyer.getId(), seller.getId());
    }

    public SellerReview createSellerReview(long buyerId) {
        User seller = createSeller();
        return createSellerReview(buyerId, seller.getId());
    }

    public SellerReview createSellerReview(long buyerId, long sellerId) {
        return sellerReviewService.create(
                SellerReview.builder()
                        .authorId(buyerId)
                        .sellerId(sellerId)
                        .rating(3)
                        .comment(createRandomString())
                        .build()
        );
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
        User buyer = createBuyer();
        return createPendingDeal(buyer);
    }

    public Deal createPendingDeal(User buyer) {
        User seller = createSeller();
        return createPendingDeal(buyer, seller);
    }

    public Deal createCompletedDeal(User buyer) {
        User seller = createSeller();
        return createDeal(buyer, seller, DealStatus.COMPLETED);
    }

    public Deal createCompletedDeal(User buyer, User seller) {
        return createDeal(buyer, seller, DealStatus.COMPLETED);
    }

    public Deal createPendingDeal(User buyer, User seller) {
        return createDeal(buyer, seller, DealStatus.PENDING);
    }

    public Deal createDeal(User buyer, User seller, DealStatus status) {
        Listing listing = createOpenListing(seller);
        Deal deal = dealRepository.save(Deal.builder()
                .buyer(buyer)
                .listing(listing)
                .totalPrice(BigDecimal.valueOf(createRandomPositiveInt()))
                .createdAt(LocalDateTime.now())
                .build());
        deal.setStatus(status);
        return dealRepository.save(deal);
    }

    public DealReview createDealReview() {
        Deal deal = createPendingDeal();
        return createDealReview(deal);
    }

    public DealReview createDealReview(Deal deal) {
        return dealReviewRepository.save(
                DealReview.builder()
                        .deal(deal)
                        .rating(3)
                        .comment(createRandomString())
                        .createdAt(LocalDateTime.now())
                        .build()

        );
    }

    public DealReview createDealReview(User buyer) {
        Deal deal = createPendingDeal(buyer);
        return createDealReview(deal);
    }
}
