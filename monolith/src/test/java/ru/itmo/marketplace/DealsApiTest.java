package ru.itmo.marketplace;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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
import ru.itmo.marketplace.entity.Deal;
import ru.itmo.marketplace.entity.DealStatus;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.dto.DealCreateRequestDto;
import ru.itmo.marketplace.dto.DealResponseDto;
import ru.itmo.marketplace.dto.DealStatusDto;
import ru.itmo.marketplace.dto.DealStatusUpdateRequestDto;
import ru.itmo.marketplace.service.DealService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DealsApiTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DealService dealService;

    @Autowired
    private TestUtils testUtils;

    private static final long UNKNOWN_ID = 9999999L;

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createDeal__validDeal_dealCreatedSuccessfully() {
        // Логика тестирования создания сделки
        User buyer = testUtils.createBuyer();
        Listing listing = testUtils.createOpenListing();

        DealCreateRequestDto dealCreateRequestDto = DealCreateRequestDto.builder()
                .totalPrice(BigDecimal.valueOf(42))
                .listingId(listing.getId())
                .build();

        String content = mockMvc.perform(
                        post("/api/v1/deals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(dealCreateRequestDto.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(dealCreateRequestDto.getListingId());
        assertThat(dealResponseDto.getBuyerId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(DealStatusDto.PENDING);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createDeal__dealAlreadyExists_errorReturned() {
        // Логика тестирования попытки создания сделки, которая уже существует для данного listing_id
        User buyer = testUtils.createBuyer();
        Deal deal = testUtils.createPendingDeal(buyer);
        Listing listing = deal.getListing();

        DealCreateRequestDto dealCreateRequestDto = DealCreateRequestDto.builder()
                .totalPrice(BigDecimal.valueOf(42))
                .listingId(listing.getId())
                .build();

        mockMvc.perform(
                        post("/api/v1/deals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createDeal__listingNotApproved_errorReturned() {
        // Логика тестирования попытки создания сделки, если объявление не в статусе APPROVED
        User buyer = testUtils.createBuyer();
        Listing listing = testUtils.createReviewListing();

        DealCreateRequestDto dealCreateRequestDto = DealCreateRequestDto.builder()
                .totalPrice(BigDecimal.valueOf(42))
                .listingId(listing.getId())
                .build();

        mockMvc.perform(
                        post("/api/v1/deals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createDeal__notBuyer_errorReturned() {
        // Логика тестирования попытки создания сделки, когда пользователь не является покупателем
        User seller = testUtils.createSeller();
        Listing listing = testUtils.createReviewListing();

        DealCreateRequestDto dealCreateRequestDto = DealCreateRequestDto.builder()
                .totalPrice(BigDecimal.valueOf(42))
                .listingId(listing.getId())
                .build();

        mockMvc.perform(
                        post("/api/v1/deals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", seller.getId())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postDeals__listingNotFound_errorReturned() {
        // Логика тестирования попытки создания сделки при отсутствии объявления с указанным listing_id
        User seller = testUtils.createSeller();

        DealCreateRequestDto dealCreateRequestDto = DealCreateRequestDto.builder()
                .totalPrice(BigDecimal.valueOf(42))
                .listingId(UNKNOWN_ID)
                .build();

        mockMvc.perform(
                        post("/api/v1/deals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", seller.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getPaginatedDeals__validConditions_dealsRetrievedSuccessfully() {
        // Логика тестирования получения списка сделок с фильтрацией по статусу

        User buyer = testUtils.createBuyer();
        Deal pendingDeal = testUtils.createPendingDeal(buyer);
        Deal completedDeal = testUtils.createCompletedDeal(buyer);

        String content = mockMvc.perform(
                        get("/api/v1/deals?status={status}", DealStatus.PENDING)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<DealResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<DealResponseDto>>() {}
        );

        assertThat(responseDto.size()).isEqualTo(1);
        TestCheckUtils.checkDeal(responseDto.get(0), pendingDeal);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealById__validId_dealRetrievedSuccessfully() {
        // Логика тестирования получения сделки по ID
        User buyer = testUtils.createBuyer();
        Deal pendingDeal = testUtils.createPendingDeal(buyer);

        String content = mockMvc.perform(
                        get("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getId()).isEqualTo(pendingDeal.getId());
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyerId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getCreatedAt()).isEqualTo(pendingDeal.getCreatedAt());
        assertThat(dealResponseDto.getStatus()).isEqualTo(DealStatusDto.PENDING);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealById__notPartyToDeal_errorReturned() {
        // Логика тестирования попытки получения сделки, когда пользователь не является ни покупателем, ни продавцом
        User buyer = testUtils.createBuyer();
        Deal pendingDeal = testUtils.createPendingDeal(buyer);
        User anotherBuyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", anotherBuyer.getId())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealById__nonExistentId_errorReturned() {
        // Логика тестирования получения ошибки, если сделки с указанным ID не существует
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/deals/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putDealById__setCompleted_statusUpdatedSuccessfully() {
        // Логика тестирования обновления статуса сделки по ID
        User buyer = testUtils.createBuyer();
        Deal pendingDeal = testUtils.createPendingDeal(buyer);

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.COMPLETED)
                .build();

        String content = mockMvc.perform(
                        put("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyerId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(dealCreateRequestDto.getStatus());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void updateDealStatus__setCanceledByBuyer_statusUpdatedSuccessfully() {
        // Логика тестирования обновления статуса сделки по ID
        User buyer = testUtils.createBuyer();
        Deal pendingDeal = testUtils.createPendingDeal(buyer);

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        String content = mockMvc.perform(
                        put("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyerId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(dealCreateRequestDto.getStatus());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void updateDealStatus__setCanceledBySeller_statusUpdatedSuccessfully() {
        // Логика тестирования обновления статуса сделки по ID
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();
        Deal pendingDeal = testUtils.createPendingDeal(buyer, seller);

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        String content = mockMvc.perform(
                        put("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", seller.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyerId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(dealCreateRequestDto.getStatus());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void updateDealStatus__invalidStatusTransitionToPending_errorReturned() {
        // Логика тестирования попытки обновления статуса сделки с неверным переходом
        User buyer = testUtils.createBuyer();
        Deal completedDeal = testUtils.createCompletedDeal(buyer);

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.PENDING)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", completedDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void updateDealStatus__invalidStatusTransitionToCanceled_errorReturned() {
        // Логика тестирования попытки обновления статуса сделки с неверным переходом
        User buyer = testUtils.createBuyer();
        Deal completedDeal = testUtils.createCompletedDeal(buyer);

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", completedDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putDealById__notPartyToDeal_errorReturned() {
        // Логика тестирования попытки обновления статуса сделки, если пользователь не является ни покупателем, ни
        // продавцом
        User buyer = testUtils.createBuyer();
        Deal completedDeal = testUtils.createPendingDeal(buyer);
        User anotherBuyer = testUtils.createBuyer();

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", completedDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", anotherBuyer.getId())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putDealById__nonExistentId_errorReturned() {
        // Логика тестирования обновления статуса сделки, если сделки с указанным ID не существует
        User buyer = testUtils.createBuyer();

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
