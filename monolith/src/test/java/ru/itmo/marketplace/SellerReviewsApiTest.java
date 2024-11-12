package ru.itmo.marketplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import ru.itmo.marketplace.entity.SellerReview;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.dto.SellerReviewCreateRequestDto;
import ru.itmo.marketplace.dto.SellerReviewUpdateRequestDto;
import ru.itmo.marketplace.service.SellerReviewService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SellerReviewsApiTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SellerReviewService sellerReviewService;

    @Autowired
    private TestUtils testUtils;

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postSellerReview__validReview_reviewCreatedSuccessfully() {
        // Логика тестирования создания отзыва на продавца
        User seller = testUtils.createSeller();
        User buyer = testUtils.createBuyer();

        SellerReviewCreateRequestDto sellerReviewCreateRequestDto = SellerReviewCreateRequestDto.builder()
                .rating(3)
                .sellerId(seller.getId())
                .comment("Great Seller!")
                .build();

        mockMvc.perform(
                        post("/api/v1/seller-reviews")
                                .header("X-User-Id", buyer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sellerReviewCreateRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller.id", is(sellerReviewCreateRequestDto.getSellerId().intValue())))
                .andExpect(jsonPath("$.rating", is(sellerReviewCreateRequestDto.getRating())))
                .andExpect(jsonPath("$.comment", is(sellerReviewCreateRequestDto.getComment())));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postSellerReview__invalidSeller() {
        User buyer = testUtils.createBuyer();

        SellerReviewCreateRequestDto sellerReviewCreateRequestDto = SellerReviewCreateRequestDto.builder()
                .rating(3)
                .sellerId(99999999L)
                .comment("Great Seller!")
                .build();

        mockMvc.perform(
                        post("/api/v1/seller-reviews")
                                .header("X-User-Id", buyer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sellerReviewCreateRequestDto))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postSellerReview__reviewExists_errorReturned() {
        User buyer = testUtils.createBuyer();
        SellerReview sellerReview = testUtils.createSellerReview(buyer.getId());

        SellerReviewCreateRequestDto sellerReviewCreateRequestDto = SellerReviewCreateRequestDto.builder()
                .rating(3)
                .sellerId(sellerReview.getSellerId())
                .comment("Great Seller!")
                .build();

        mockMvc.perform(
                        post("/api/v1/seller-reviews")
                                .header("X-User-Id", buyer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sellerReviewCreateRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getPaginatedSellerReviews__validConditions_reviewsRetrievedSuccessfully() {
        // Логика тестирования получения отзывов пользователя на продавцов
        User buyer = testUtils.createBuyer();
        SellerReview sellerReview = testUtils.createSellerReview(buyer.getId());

        mockMvc.perform(
                        get("/api/v1/seller-reviews")
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].seller.id", is(sellerReview.getSellerId().intValue())))
                .andExpect(jsonPath("$.content[0].author.id", is(buyer.getId().intValue())))
                .andExpect(jsonPath("$.content[0].rating", is(sellerReview.getRating())))
                .andExpect(jsonPath("$.content[0].comment", is(sellerReview.getComment())))
                .andExpect(jsonPath("$.total_elements", is(1)))
                .andExpect(jsonPath("$.total_pages", is(1)));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getSellerReviewsBySellerId__validSellerId_reviewsRetrievedSuccessfully() {
        // Логика тестирования получения отзывов на продавца по ID
        User seller = testUtils.createSeller();

        User buyer1 = testUtils.createBuyer();
        User buyer2 = testUtils.createBuyer();

        SellerReview sellerReview1 = testUtils.createSellerReview(buyer1.getId(), seller.getId());
        SellerReview sellerReview2 = testUtils.createSellerReview(buyer2.getId(), seller.getId());

        mockMvc.perform(get("/api/v1/seller-reviews/{seller_id}", seller.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .header("X-User-Id", buyer1.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].seller.id", is(sellerReview1.getSellerId().intValue())))
                .andExpect(jsonPath("$.content[0].author.id", is(buyer1.getId().intValue())))
                .andExpect(jsonPath("$.content[0].rating", is(sellerReview1.getRating())))
                .andExpect(jsonPath("$.content[0].comment", is(sellerReview1.getComment())))
                .andExpect(jsonPath("$.content[1].seller.id", is(sellerReview2.getSellerId().intValue())))
                .andExpect(jsonPath("$.content[1].author.id", is(sellerReview2.getAuthorId().intValue())))
                .andExpect(jsonPath("$.content[1].rating", is(sellerReview2.getRating())))
                .andExpect(jsonPath("$.content[1].comment", is(sellerReview2.getComment())))
                .andExpect(jsonPath("$.total_elements", is(2)))
                .andExpect(jsonPath("$.total_pages", is(1)));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getSellerReviewsBySellerId__nonExistentSeller_errorReturned() {
        // Логика тестирования получения ошибки при отсутствии продавца по указанному ID
        User buyer = testUtils.createBuyer();

        mockMvc.perform(get("/api/v1/seller-reviews/{seller_id}", 99999999L)
                        .header("X-User-Id", buyer.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putSellerReviewBySellerId__validReview_reviewUpdatedSuccessfully() {
        // Логика тестирования обновления отзыва на продавца
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();
        SellerReview sellerReview = testUtils.createSellerReview(buyer.getId(), seller.getId());

        SellerReviewUpdateRequestDto dto = SellerReviewUpdateRequestDto.builder()
                .rating(2)
                .comment("Updated comment")
                .build();

        mockMvc.perform(put("/api/v1/seller-reviews/{seller_id}", seller.getId())
                        .header("X-User-Id", buyer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller.id", is(sellerReview.getSellerId().intValue())))
                .andExpect(jsonPath("$.author.id", is(sellerReview.getAuthorId().intValue())))
                .andExpect(jsonPath("$.rating", is(dto.getRating())))
                .andExpect(jsonPath("$.comment", is(dto.getComment())));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putSellerReviewBySellerId__notBuyer_errorReturned() {
        // Логика тестирования попытки обновления отзыва, когда пользователь не является покупателем
        User seller = testUtils.createSeller();
        User buyer = testUtils.createBuyer();
        SellerReview sellerReview = testUtils.createSellerReview(buyer.getId(), seller.getId());

        SellerReviewUpdateRequestDto dto = SellerReviewUpdateRequestDto.builder()
                .rating(2)
                .comment("Updated comment")
                .build();

        mockMvc.perform(put("/api/v1/seller-reviews/{seller_id}", seller.getId())
                        .header("X-User-Id", seller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putSellerReviewBySellerId__nonExistentReviewOrSeller_errorReturned() {
        // Логика тестирования обновления отзыва, когда отзыв не существует
        User seller = testUtils.createSeller();
        User buyer = testUtils.createBuyer();

        SellerReviewUpdateRequestDto dto = SellerReviewUpdateRequestDto.builder()
                .rating(2)
                .comment("Updated comment")
                .build();

        mockMvc.perform(put("/api/v1/seller-reviews/{seller_id}", seller.getId().intValue())
                        .header("X-User-Id", buyer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteSellerReviewBySellerId__validReview_reviewDeletedSuccessfully() {
        // Логика тестирования удаления отзыва на продавца
        User seller = testUtils.createSeller();
        User buyer = testUtils.createBuyer();
        SellerReview sellerReview = testUtils.createSellerReview(buyer.getId(), seller.getId());

        mockMvc.perform(delete("/api/v1/seller-reviews/{seller_id}", seller.getId())
                        .header("X-User-Id", buyer.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteSellerReviewBySellerId__nonExistentReviewOrSeller_errorReturned() {
        // Логика тестирования удаления отзыва, когда отзыв или продавец не существует

        User seller = testUtils.createSeller();
        User buyer = testUtils.createBuyer();

        mockMvc.perform(delete("/api/v1/seller-reviews/{seller_id}", seller.getId())
                        .header("X-User-Id", buyer.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
