package ru.itmo.marketplace.service.review;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.seller.SellerReviewCreateRequestDto;
import ru.itmo.common.dto.review.seller.SellerReviewResponseDto;
import ru.itmo.common.dto.review.seller.SellerReviewUpdateRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.service.user.client.UserApiReactiveClient;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class SellerReviewApiTest extends IntegrationEnvironment {
    public static final long UNKNOWN_USER_ID = 999L;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TestUtils testUtils;

    @MockBean
    UserApiReactiveClient userApiReactiveClient;

    UserResponseDto buyer;
    UserResponseDto buyer2;
    UserResponseDto seller;
    UserResponseDto seller2;
    UserResponseDto admin;

    @BeforeEach
    void setup() {
        buyer = testUtils.getBuyer();
        when(userApiReactiveClient.getUserById(buyer.getId())).thenReturn(Mono.just(buyer));
        buyer2 = testUtils.getBuyer2();
        when(userApiReactiveClient.getUserById(buyer2.getId())).thenReturn(Mono.just(buyer2));
        seller = testUtils.getSeller();
        when(userApiReactiveClient.getUserById(seller.getId())).thenReturn(Mono.just(seller));
        seller2 = testUtils.getSeller2();
        when(userApiReactiveClient.getUserById(seller2.getId())).thenReturn(Mono.just(seller2));
        admin = testUtils.getAdmin();
        when(userApiReactiveClient.getUserById(admin.getId())).thenReturn(Mono.just(admin));

        when(userApiReactiveClient.getUserById(UNKNOWN_USER_ID)).thenReturn(Mono.empty());

        var usersMap = Map.of(
                buyer.getId(), buyer,
                seller.getId(), seller,
                admin.getId(), admin
        );

        when(userApiReactiveClient.getUsersByIds(anyList())).thenAnswer(invocation -> {
            List<Long> ids = (List<Long>) invocation.getArguments()[0];
            List<UserResponseDto> list = ids.stream().map(usersMap::get).toList();
            return Flux.fromIterable(list);
        });
    }


    // ### POST /api/v1/reviews/sellers ###

    @Test
    @SneakyThrows
    @Transactional
    void createSellerReview__validRequest_reviewCreatedSuccessfully() {
        SellerReviewCreateRequestDto request = new SellerReviewCreateRequestDto();
        request.setSellerId(seller.getId());
        request.setRating(5);
        request.setComment("Great seller!");

        webTestClient.post()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @SneakyThrows
    @Transactional
    void createSellerReview__duplicateReview_badRequestReturned() {
        testUtils.createSellerReview(buyer, seller);

        SellerReviewCreateRequestDto request = new SellerReviewCreateRequestDto();
        request.setSellerId(seller.getId());
        request.setRating(5);
        request.setComment("Another review!");

        webTestClient.post()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @SneakyThrows
    @Transactional
    void createSellerReview__notBuyer_forbiddenReturned() {
        SellerReviewCreateRequestDto request = new SellerReviewCreateRequestDto();
        request.setSellerId(seller.getId());
        request.setRating(4);
        request.setComment("Good seller.");

        webTestClient.post()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", seller.getId().toString())
                .header("X-User-Role", seller.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @SneakyThrows
    @Transactional
    void createSellerReview__sellerNotFound_notFoundReturned() {
        SellerReviewCreateRequestDto request = new SellerReviewCreateRequestDto();
        request.setSellerId(UNKNOWN_USER_ID);
        request.setRating(3);
        request.setComment("Average seller.");

        webTestClient.post()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### GET /api/v1/reviews/sellers ###

    @Test
    @SneakyThrows
    @Transactional
    void getBuyerReviews__validRequest_reviewsReturned() {
        testUtils.createSellerReview(buyer, seller);

        webTestClient.get()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole().toString())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SellerReviewResponseDto.class)
                .hasSize(1);
    }

    @Test
    @SneakyThrows
    @Transactional
    void getBuyerReviews__notBuyer_forbiddenReturned() {
        webTestClient.get()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", seller.getId().toString())
                .header("X-User-Role", seller.getRole())
                .exchange()
                .expectStatus().isForbidden();
    }


    // ### GET /api/v1/reviews/sellers/{seller_id} ###

    @Test
    @SneakyThrows
    @Transactional
    void getSellerReviews__validRequest_reviewsReturned() {
        testUtils.createSellerReview(buyer, seller);

        webTestClient.get()
                .uri("/api/v1/reviews/sellers/" + seller.getId())
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SellerReviewResponseDto.class)
                .hasSize(1);
    }

    @Test
    @SneakyThrows
    @Transactional
    void getSellerReviews__sellerNotFound_notFoundReturned() {
        webTestClient.get()
                .uri("/api/v1/reviews/sellers/" + UNKNOWN_USER_ID)
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### PUT /api/v1/reviews/sellers/{seller_id} ###

    @Test
    @SneakyThrows
    @Transactional
    void updateReview__validRequest_reviewUpdatedSuccessfully() {
        testUtils.createSellerReview(buyer, seller);

        SellerReviewUpdateRequestDto request = new SellerReviewUpdateRequestDto();
        request.setRating(4);
        request.setComment("Updated comment.");

        webTestClient.put()
                .uri("/api/v1/reviews/sellers/" + seller.getId())
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @SneakyThrows
    @Transactional
    void updateReview__notBuyer_forbiddenReturned() {
        SellerReviewUpdateRequestDto request = new SellerReviewUpdateRequestDto();
        request.setRating(3);
        request.setComment("Another update.");

        webTestClient.put()
                .uri("/api/v1/reviews/sellers/" + seller.getId())
                .header("X-User-Id", seller.getId().toString())
                .header("X-User-Role", seller.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @SneakyThrows
    @Transactional
    void updateReview__sellerNotFound_notFoundReturned() {
        SellerReviewUpdateRequestDto request = new SellerReviewUpdateRequestDto();
        request.setRating(2);
        request.setComment("Unknown seller.");

        webTestClient.put()
                .uri("/api/v1/reviews/sellers/" + UNKNOWN_USER_ID)
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @SneakyThrows
    @Transactional
    void updateReview__reviewNotFound_notFoundReturned() {
        SellerReviewUpdateRequestDto request = new SellerReviewUpdateRequestDto();
        request.setRating(5);
        request.setComment("Review does not exist.");

        webTestClient.put()
                .uri("/api/v1/reviews/sellers/" + seller.getId())
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### DELETE /api/v1/reviews/sellers/{seller_id} ###

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__validRequest_reviewDeletedSuccessfully() {
        testUtils.createSellerReview(buyer, seller);

        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + seller.getId())
                .header("X-User-Id", buyer.getId().toString())
                .header("X-User-Role", buyer.getRole())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__notBuyer_forbiddenReturned() {
        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + seller2.getId())
                .header("X-User-Id", seller2.getId().toString())
                .header("X-User-Role", seller2.getRole())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__reviewNotFound_notFoundReturned() {
        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + seller2.getId())
                .header("X-User-Id", buyer2.getId().toString())
                .header("X-User-Role", buyer2.getRole())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__sellerNotFound_notFoundReturned() {
        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + UNKNOWN_USER_ID)
                .header("X-User-Id", buyer2.getId().toString())
                .header("X-User-Role", buyer2.getRole())
                .exchange()
                .expectStatus().isNotFound();
    }

}
