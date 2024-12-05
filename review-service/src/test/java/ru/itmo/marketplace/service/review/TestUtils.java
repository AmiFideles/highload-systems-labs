package ru.itmo.marketplace.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;
import ru.itmo.marketplace.service.review.repository.SellerReviewRepository;

import java.util.List;

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

    public UserResponseDto ADMIN = UserResponseDto.builder().id(1L).role("ADMIN").build();
    public UserResponseDto MODERATOR = UserResponseDto.builder().id(2L).role("MODERATOR").build();

    public UserResponseDto BUYER = UserResponseDto.builder().id(3L).role("BUYER").build();
    public UserResponseDto SELLER = UserResponseDto.builder().id(4L).role("SELLER").build();

    public UserResponseDto BUYER2 = UserResponseDto.builder().id(5L).role("BUYER").build();
    public UserResponseDto SELLER2 = UserResponseDto.builder().id(6L).role("SELLER").build();

    public UserResponseDto BUYER3 = UserResponseDto.builder().id(7L).role("BUYER").build();
    public UserResponseDto SELLER3 = UserResponseDto.builder().id(8L).role("SELLER").build();

    public UserResponseDto BUYER4 = UserResponseDto.builder().id(9L).role("BUYER").build();
    public UserResponseDto SELLER4 = UserResponseDto.builder().id(10L).role("SELLER").build();

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

}
