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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.review.deal.DealReviewRequestDto;
import ru.itmo.common.dto.review.deal.DealReviewResponseDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.service.market.client.DealApiReactiveClient;
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
class DealReviewApiTest extends IntegrationEnvironment {

    public static final long UNKNOWN_USER_ID = 999L;

    @Autowired
    TestUtils testUtils;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    UserApiReactiveClient userApiReactiveClient;

    @MockBean
    DealApiReactiveClient dealApiReactiveClient;

    @BeforeEach
    void setup() {
        var usersMap = testUtils.USERS.stream().collect(Collectors.toMap(
                UserResponseDto::getId,
                Function.identity()
        ));

        testUtils.USERS.forEach(user ->
                when(userApiReactiveClient.getUserById(user.getId())).thenReturn(Mono.just(user))
        );
        when(userApiReactiveClient.getUserById(UNKNOWN_USER_ID)).thenReturn(Mono.empty());
        when(userApiReactiveClient.getUsersByIds(anyList())).thenAnswer(invocation -> {
            List<Long> ids = (List<Long>) invocation.getArguments()[0];
            List<UserResponseDto> list = ids.stream().map(usersMap::get).toList();
            return Flux.fromIterable(list);
        });

        var dealsMap = testUtils.DEALS.stream().collect(Collectors.toMap(
                DealResponseDto::getId,
                Function.identity()
        ));

        when(dealApiReactiveClient.getDealById(testUtils.VALID_DEAL_ID))
                .thenReturn(Mono.just(testUtils.VALID_DEAL));
        when(dealApiReactiveClient.getDealById(testUtils.EXISTING_REVIEW_DEAL_ID))
                .thenReturn(Mono.just(testUtils.EXISTING_REVIEW_DEAL));
        when(dealApiReactiveClient.getDealById(testUtils.WITHOUT_REVIEW_DEAL_ID))
                .thenReturn(Mono.just(testUtils.WITHOUT_REVIEW_DEAL));
        when(dealApiReactiveClient.getDealById(testUtils.NON_EXISTENT_DEAL_ID)).thenReturn(Mono.empty());
        when(dealApiReactiveClient.getDealsByIds(anyList())).thenAnswer(invocation -> {
            List<Long> ids = (List<Long>) invocation.getArguments()[0];
            List<DealResponseDto> list = ids.stream().map(dealsMap::get).toList();
            return Flux.fromIterable(list);
        });
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void createDealReview__validRequest_reviewCreatedSuccessfully() {
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.VALID_DEAL_ID);
        request.setRating(5);
        request.setComment("Excellent deal!");

        webTestClient.post()
                .uri("/api/v1/reviews/deals")
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void createDealReview__reviewAlreadyExists_error400() {
        // Assuming the review already exists for this deal
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.EXISTING_REVIEW_DEAL_ID);
        request.setRating(3);
        request.setComment("Average deal");

        webTestClient.post()
                .uri("/api/v1/reviews/deals")
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void createDealReview__userNotBuyer_error403() {
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.VALID_DEAL_ID);
        request.setRating(4);
        request.setComment("Good deal!");

        webTestClient.post()
                .uri("/api/v1/reviews/deals")
                .header("X-User-Id", testUtils.SELLER.getId().toString())
                .header("X-User-Role", testUtils.SELLER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void createDealReview__dealDoesNotExist_error404() {
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.NON_EXISTENT_DEAL_ID);
        request.setRating(3);
        request.setComment("Unreal deal");

        webTestClient.post()
                .uri("/api/v1/reviews/deals")
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### GET /api/v1/reviews/deals ###

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void getDealReviews__validRequest_reviewsFetchedSuccessfully() {
        webTestClient.get()
                .uri("/api/v1/reviews/deals")
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void getDealReviews__userNotBuyer_error403() {
        webTestClient.get()
                .uri("/api/v1/reviews/deals")
                .header("X-User-Id", testUtils.SELLER.getId().toString())
                .header("X-User-Role", testUtils.SELLER.getRole().toString())
                .exchange()
                .expectStatus().isForbidden();
    }


    // ### GET /api/v1/reviews/deals/{deal_id} ###

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void getDealReview__validRequest_reviewFetchedSuccessfully() {
        webTestClient.get()
                .uri("/api/v1/reviews/deals/" + testUtils.VALID_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(DealReviewResponseDto.class);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void getDealReview__dealDoesNotExist_error404() {
        webTestClient.get()
                .uri("/api/v1/reviews/deals/" + testUtils.NON_EXISTENT_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### PUT /api/v1/reviews/deals/{deal_id} ###

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void updateDealReview__validRequest_reviewUpdatedSuccessfully() {
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.VALID_DEAL_ID);
        request.setRating(4);
        request.setComment("Updated review");

        webTestClient.put()
                .uri("/api/v1/reviews/deals/" + testUtils.VALID_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void updateDealReview__userNotBuyer_error403() {
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.VALID_DEAL_ID);
        request.setRating(4);
        request.setComment("Unauthorized update");

        webTestClient.put()
                .uri("/api/v1/reviews/deals/" + testUtils.VALID_DEAL_ID)
                .header("X-User-Id", testUtils.SELLER.getId().toString())
                .header("X-User-Role", testUtils.SELLER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void updateDealReview__dealDoesNotExist_error404() {
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.NON_EXISTENT_DEAL_ID);
        request.setRating(4);
        request.setComment("Non-existent deal");

        webTestClient.put()
                .uri("/api/v1/reviews/deals/" + testUtils.NON_EXISTENT_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void updateDealReview__reviewDoesNotExist_error404() {
        DealReviewRequestDto request = new DealReviewRequestDto();
        request.setDealId(testUtils.WITHOUT_REVIEW_DEAL_ID);
        request.setRating(4);
        request.setComment("No review exists for this deal");

        webTestClient.put()
                .uri("/api/v1/reviews/deals/" + testUtils.WITHOUT_REVIEW_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }


    // ### DELETE /api/v1/reviews/deals/{deal_id} ###

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void deleteDealReview__validRequest_reviewDeletedSuccessfully() {
        webTestClient.delete()
                .uri("/api/v1/reviews/deals/" + testUtils.VALID_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void deleteDealReview__userNotBuyer_error403() {
        webTestClient.delete()
                .uri("/api/v1/reviews/deals/" + testUtils.VALID_DEAL_ID)
                .header("X-User-Id", testUtils.SELLER.getId().toString())
                .header("X-User-Role", testUtils.SELLER.getRole().toString())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void deleteDealReview__reviewDoesNotExist_error404() {
        webTestClient.delete()
                .uri("/api/v1/reviews/deals/" + testUtils.VALID_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void deleteDealReview__dealDoesNotExist_error404() {
        webTestClient.delete()
                .uri("/api/v1/reviews/deals/" + testUtils.VALID_DEAL_ID)
                .header("X-User-Id", testUtils.BUYER.getId().toString())
                .header("X-User-Role", testUtils.BUYER.getRole().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

}
