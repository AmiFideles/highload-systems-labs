package ru.itmo.marketplace.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;
import ru.itmo.marketplace.service.review.repository.SellerReviewRepository;

@Component
public class TestUtils {
    @Autowired
    private SellerReviewRepository sellerReviewRepository;

    public SellerReviewEntity createSellerReview(
            UserResponseDto buyer,
            UserResponseDto seller
    ) {
        return sellerReviewRepository.save(
                SellerReviewEntity.builder()
                        .authorId(buyer.getId())
                        .sellerId(seller.getId())
                        .rating(3)
                        .comment("comment")
                        .build()
        );
    }

    public UserResponseDto getAdmin() {
        return UserResponseDto.builder()
                .id(1L)
                .role("ADMIN")
                .email("admin@admin")
                .name("admin")
                .build();
    }

    public UserResponseDto getBuyer() {
        return UserResponseDto.builder()
                .id(3L)
                .role("BUYER")
                .email("buyer@email.com")
                .name("buyer")
                .build();
    }

    public UserResponseDto getBuyer2() {
        return UserResponseDto.builder()
                .id(5L)
                .role("BUYER")
                .email("buyer@email.com")
                .name("buyer")
                .build();
    }

    public UserResponseDto getSeller2() {
        return UserResponseDto.builder()
                .id(6L)
                .role("SELLER")
                .email("seller@email.com")
                .name("seller")
                .build();
    }

    public UserResponseDto getSeller() {
        return UserResponseDto.builder()
                .id(4L)
                .role("SELLER")
                .email("seller@email.com")
                .name("seller")
                .build();
    }
}
