package ru.itmo.marketplace;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealReview;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.dto.DealReviewRequestDto;
import ru.itmo.marketplace.dto.DealReviewResponseDto;
import ru.itmo.marketplace.service.DealReviewService;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DealReviewsApiTest extends IntegrationEnvironment {
    private static final Long UNKNOWN_ID = 9999999L;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DealReviewService dealReviewService;

    @Autowired
    private TestUtils testUtils;

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postDealReviews__validDealReview_reviewCreatedSuccessfully() {
        // Логика тестирования создания отзыва на сделку
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();

        Deal deal = testUtils.createPendingDeal(buyer, seller);

        DealReviewRequestDto requestDto = getDealReviewRequestDto();
        requestDto.setDealId(deal.getId());
        String content = mockMvc.perform(
                        post("/api/v1/deal-reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DealReviewResponseDto responseDto = objectMapper.readValue(content, DealReviewResponseDto.class);
        assertThat(responseDto.getRating()).isEqualTo(requestDto.getRating());
        assertThat(responseDto.getComment()).isEqualTo(requestDto.getComment());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postDealReviews__reviewAlreadyExists_errorReturned() {
        // Логика тестирования попытки создания отзыва, когда он уже существует
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();

        Deal deal = testUtils.createPendingDeal(buyer, seller);
        testUtils.createDealReview(deal);

        DealReviewRequestDto requestDto = getDealReviewRequestDto();

        mockMvc.perform(
                        post("/api/v1/deal-reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postDealReviews__nonExistentDeal_errorReturned() {
        // Логика тестирования попытки создания отзыва для несуществующей сделки
        User buyer = testUtils.createBuyer();

        DealReviewRequestDto requestDto = getDealReviewRequestDto();
        requestDto.setDealId(1L);
        mockMvc.perform(
                        post("/api/v1/deal-reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postDealReviews__invalidDealIdOrNotBuyer_errorReturned() {
        // Логика тестирования попытки создания отзыва когда пользователь не является покупателем
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();
        Deal deal = testUtils.createPendingDeal(buyer, seller);

        User anotherBuyer = testUtils.createBuyer();

        DealReviewRequestDto requestDto = getDealReviewRequestDto();
        requestDto.setDealId(deal.getId());
        mockMvc.perform(
                        post("/api/v1/deal-reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", anotherBuyer.getId())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getPaginatedDealReviews__validConditions_reviewsRetrievedSuccessfully() {
        // Логика тестирования получения отзывов пользователя на сделки
        User buyer = testUtils.createBuyer();
        List<DealReview> reviewList = IntStream.range(0, 10).mapToObj(it -> testUtils.createDealReview(buyer)).toList();

        String content = mockMvc.perform(
                        get("/api/v1/deal-reviews")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId().intValue())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<DealReviewResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<DealReviewResponseDto>>() {}
        );
        assertThat(responseDto.size()).isEqualTo(10);
        IntStream.range(0, 10).forEach(i -> compare(reviewList.get(i), responseDto.get(i)));
    }

    private void compare(DealReview dealReview, DealReviewResponseDto dealReviewResponseDto) {
        assertThat(dealReview.getId()).isEqualTo(dealReviewResponseDto.getId());
        assertThat(dealReview.getComment()).isEqualTo(dealReviewResponseDto.getComment());
        assertThat(dealReview.getRating()).isEqualTo(dealReviewResponseDto.getRating());
        assertThat(dealReview.getCreatedAt()).isEqualTo(dealReviewResponseDto.getCreatedAt());
        assertThat(dealReview.getDeal().getId()).isEqualTo(dealReviewResponseDto.getDeal().getId());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getPaginatedDealReviews__notBuyer_errorReturned() {
        // Логика тестирования попытки получения отзывов, когда пользователь не является покупателем
        User buyer = testUtils.createBuyer();
        testUtils.createDealReview(buyer);
        testUtils.createDealReview(buyer);
        testUtils.createDealReview(buyer);

        User seller = testUtils.createSeller();

        mockMvc.perform(
                        get("/api/v1/deal-reviews")
                                .header("X-User-Id", seller.getId())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealReviewById__validDealId_reviewRetrievedSuccessfully() {
        // Логика тестирования получения отзыва на сделку по ID
        User buyer = testUtils.createBuyer();
        Deal deal = testUtils.createPendingDeal(buyer);
        DealReview dealReview = testUtils.createDealReview(deal);

        String content = mockMvc.perform(
                        get("/api/v1/deal-reviews/{id}", deal.getId())
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealReviewResponseDto responseDto = objectMapper.readValue(content, DealReviewResponseDto.class);

        assertThat(responseDto.getDeal().getId()).isEqualTo(dealReview.getDeal().getId());
        assertThat(responseDto.getRating()).isEqualTo(dealReview.getRating());
        assertThat(responseDto.getComment()).isEqualTo(dealReview.getComment());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealReviewById__nonExistentReview_errorReturned() {
        // Логика тестирования получения ошибки при отсутствии отзыва для указанной сделки
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/deal-reviews/{id}", UNKNOWN_ID)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putDealReviewById__validDealId_reviewUpdatedSuccessfully() {
        // Логика тестирования обновления отзыва на сделку
        User buyer = testUtils.createBuyer();
        Deal deal = testUtils.createPendingDeal(buyer);
        testUtils.createDealReview(deal);

        DealReviewRequestDto requestDto = getDealReviewRequestDto();
        requestDto.setDealId(deal.getId());
        String content = mockMvc.perform(
                        put("/api/v1/deal-reviews/{id}", deal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealReviewResponseDto responseDto = objectMapper.readValue(content, DealReviewResponseDto.class);

        assertThat(responseDto.getRating()).isEqualTo(requestDto.getRating());
        assertThat(responseDto.getComment()).isEqualTo(requestDto.getComment());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putDealReviewById__nonExistentDeal_errorReturned() {
        // Логика тестирования обновления отзыва на несуществующую сделку
        User buyer = testUtils.createBuyer();

        DealReviewRequestDto requestDto = getDealReviewRequestDto();
        requestDto.setDealId(1L);
        mockMvc.perform(
                        put("/api/v1/deal-reviews/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putDealReviewById__foreignDealOrNotBuyer_errorReturned() {
        // Логика тестирования попытки обновления отзыва когда пользователь не является покупателем
        User buyer = testUtils.createBuyer();
        Deal deal = testUtils.createPendingDeal(buyer);
        testUtils.createDealReview(deal);

        User anotherBuyer = testUtils.createBuyer();

        DealReviewRequestDto requestDto = getDealReviewRequestDto();
        requestDto.setDealId(deal.getId());
        mockMvc.perform(
                        put("/api/v1/deal-reviews/{id}", deal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", anotherBuyer.getId())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteDealReviewById__validDealId_reviewDeletedSuccessfully() {
        // Логика тестирования удаления отзыва на сделку
        User buyer = testUtils.createBuyer();
        DealReview dealReview = testUtils.createDealReview(buyer);

        mockMvc.perform(
                        delete("/api/v1/deal-reviews/{id}", dealReview.getId())
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteDealReviewById__moderatorValidDealId_reviewDeletedSuccessfully() {
        // Логика тестирования удаления отзыва на сделку
        User buyer = testUtils.createBuyer();
        User moderator = testUtils.createModerator();
        DealReview dealReview = testUtils.createDealReview(buyer);

        mockMvc.perform(
                        delete("/api/v1/deal-reviews/{id}", dealReview.getId())
                                .header("X-User-Id", moderator.getId())
                )
                .andDo(print())
                .andExpect(status().is(204));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteDealReviewById__nonExistentDeal_errorReturned() {
        // Логика тестирования удаления отзыва на несуществующую сделку
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        delete("/api/v1/deal-reviews/{id}", UNKNOWN_ID)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteDealReviewById__foreignDealOrNotAuthorized_errorReturned() {
        // Логика тестирования удаления отзыва на чужую сделку или когда нет прав (не покупатель/модератор/админ)
        User buyer = testUtils.createBuyer();
        DealReview dealReview = testUtils.createDealReview(buyer);

        User anotherBuyer = testUtils.createBuyer();
        mockMvc.perform(
                        delete("/api/v1/deal-reviews/{id}", dealReview.getId())
                                .header("X-User-Id", anotherBuyer.getId())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private static DealReviewRequestDto getDealReviewRequestDto() {
        DealReviewRequestDto requestDto = DealReviewRequestDto.builder()
                .rating(3)
                .comment("Deal review comment")
                .build();
        return requestDto;
    }

}
