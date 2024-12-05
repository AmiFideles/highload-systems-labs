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
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @BeforeEach
    void setup() {
        testUtils.USERS.forEach(user ->
                when(userApiReactiveClient.getUserById(user.getId())).thenReturn(Mono.just(user))
        );

        when(userApiReactiveClient.getUserById(UNKNOWN_USER_ID)).thenReturn(Mono.empty());

        var usersMap = testUtils.USERS.stream().collect(Collectors.toMap(
                UserResponseDto::getId,
                Function.identity()
        ));

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
        request.setSellerId(testUtils.SELLER.getId());
        request.setRating(5);
        request.setComment("Great seller!");

        webTestClient.post()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @SneakyThrows
    @Transactional
    void createSellerReview__duplicateReview_badRequestReturned() {
        SellerReviewCreateRequestDto request = new SellerReviewCreateRequestDto();
        request.setSellerId(testUtils.SELLER3.getId());
        request.setRating(5);
        request.setComment("Another review!");

        webTestClient.post()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", testUtils.BUYER3.getId().toString())
                .header("X-User-Role", testUtils.BUYER3.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @SneakyThrows
    @Transactional
    void createSellerReview__notBuyer_forbiddenReturned() {
        SellerReviewCreateRequestDto request = new SellerReviewCreateRequestDto();
        request.setSellerId(testUtils.SELLER.getId());
        request.setRating(4);
        request.setComment("Good seller.");

        webTestClient.post()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", testUtils.SELLER.getId().toString())
                .header("X-User-Role", testUtils.SELLER.getRole())
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
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### GET /api/v1/reviews/sellers ###

    @Test
    @SneakyThrows
    @Transactional
    void getBuyerReviews__validRequest_reviewsReturned() {
        testUtils.createSellerReview(testUtils.BUYER, testUtils.SELLER);

        webTestClient.get()
                .uri("/api/v1/reviews/sellers")
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
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
                .header("X-User-Id", testUtils.SELLER.getId().toString())
                .header("X-User-Role", testUtils.SELLER.getRole())
                .exchange()
                .expectStatus().isForbidden();
    }


    // ### GET /api/v1/reviews/sellers/{seller_id} ###

    @Test
    @SneakyThrows
    @Transactional
    void getSellerReviews__validRequest_reviewsReturned() {
        testUtils.createSellerReview(testUtils.BUYER, testUtils.SELLER);

        webTestClient.get()
                .uri("/api/v1/reviews/sellers/" + testUtils.SELLER.getId())
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole())
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
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole())
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### PUT /api/v1/reviews/sellers/{seller_id} ###

    @Test
    @SneakyThrows
    @Transactional
    void updateReview__validRequest_reviewUpdatedSuccessfully() {
        SellerReviewUpdateRequestDto request = new SellerReviewUpdateRequestDto();
        request.setRating(4);
        request.setComment("Updated comment.");

        webTestClient.put()
                .uri("/api/v1/reviews/sellers/" + testUtils.SELLER4.getId())
                .header("X-User-Id", testUtils.BUYER4.getId().toString())
                .header("X-User-Role", testUtils.BUYER4.getRole())
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
                .uri("/api/v1/reviews/sellers/" + testUtils.SELLER.getId())
                .header("X-User-Id", testUtils.SELLER.getId().toString())
                .header("X-User-Role", testUtils.SELLER.getRole())
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
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole())
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
                .uri("/api/v1/reviews/sellers/" + testUtils.SELLER.getId())
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### DELETE /api/v1/reviews/sellers/{seller_id} ###

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__validRequest_reviewDeletedSuccessfully() {
        testUtils.createSellerReview(testUtils.BUYER, testUtils.SELLER);

        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + testUtils.SELLER.getId())
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__notBuyer_forbiddenReturned() {
        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + testUtils.SELLER2.getId())
                .header("X-User-Id", testUtils.SELLER2.getId().toString())
                .header("X-User-Role", testUtils.SELLER2.getRole())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__reviewNotFound_notFoundReturned() {
        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + testUtils.SELLER2.getId())
                .header("X-User-Id", testUtils.BUYER2.getId().toString())
                .header("X-User-Role", testUtils.BUYER2.getRole())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteReview__sellerNotFound_notFoundReturned() {
        webTestClient.delete()
                .uri("/api/v1/reviews/sellers/" + UNKNOWN_USER_ID)
                .header("X-User-Id", testUtils.BUYER2.getId().toString())
                .header("X-User-Role", testUtils.BUYER2.getRole())
                .exchange()
                .expectStatus().isNotFound();
    }

}
