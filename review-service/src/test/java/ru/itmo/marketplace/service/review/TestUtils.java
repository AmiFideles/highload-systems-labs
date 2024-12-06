package ru.itmo.marketplace.service.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.category.CategoryResponseDto;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.deal.DealStatusDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.dto.user.UserRoleDto;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;
import ru.itmo.marketplace.service.review.repository.SellerReviewRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Long VALID_DEAL_ID = 1L;
    public Long EXISTING_REVIEW_DEAL_ID = 2L;
    public Long WITHOUT_REVIEW_DEAL_ID = 3L;
    public Long NON_EXISTENT_DEAL_ID = 999L;

    public DealResponseDto VALID_DEAL = DealResponseDto.builder()
            .id(VALID_DEAL_ID)
            .buyer(BUYER)
            .totalPrice(BigDecimal.valueOf(10))
            .status(DealStatusDto.COMPLETED)
            .listing(ListingResponseDto.builder()
                    .id(1L)
                    .title("title")
                    .description("description")
                    .price(BigDecimal.valueOf(10))
                    .categories(List.of())
                    .creator(SELLER)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .used(false)
                    .build())
            .createdAt(LocalDateTime.now())
            .build();

    public DealResponseDto EXISTING_REVIEW_DEAL = DealResponseDto.builder()
            .id(EXISTING_REVIEW_DEAL_ID)
            .buyer(BUYER2)
            .totalPrice(BigDecimal.valueOf(10))
            .status(DealStatusDto.COMPLETED)
            .listing(ListingResponseDto.builder()
                    .id(2L)
                    .title("title")
                    .description("description")
                    .price(BigDecimal.valueOf(10))
                    .categories(List.of())
                    .creator(SELLER2)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .used(false)
                    .build())
            .createdAt(LocalDateTime.now())
            .build();

    public DealResponseDto WITHOUT_REVIEW_DEAL = DealResponseDto.builder()
            .id(WITHOUT_REVIEW_DEAL_ID)
            .buyer(BUYER3)
            .totalPrice(BigDecimal.valueOf(10))
            .status(DealStatusDto.COMPLETED)
            .listing(ListingResponseDto.builder()
                    .id(3L)
                    .title("title")
                    .description("description")
                    .price(BigDecimal.valueOf(10))
                    .categories(List.of())
                    .creator(SELLER3)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .used(false)
                    .build())
            .createdAt(LocalDateTime.now())
            .build();

    public List<DealResponseDto> DEALS = List.of(
            VALID_DEAL,
            EXISTING_REVIEW_DEAL,
            WITHOUT_REVIEW_DEAL
    );

}
