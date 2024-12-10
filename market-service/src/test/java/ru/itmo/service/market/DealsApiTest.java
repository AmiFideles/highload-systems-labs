package ru.itmo.service.market;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.dto.deal.DealCreateRequestDto;
import ru.itmo.common.dto.deal.DealResponseDto;
import ru.itmo.common.dto.deal.DealStatusDto;
import ru.itmo.common.dto.deal.DealStatusUpdateRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.modules.security.UserSecurityContextHolder;
import ru.itmo.service.market.entity.Deal;
import ru.itmo.service.market.entity.DealStatus;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.service.DealService;
import ru.itmo.service.market.util.TestCheckUtils;
import ru.itmo.service.market.util.TestUtils;
import ru.itmo.service.user.client.UserApiClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DealsApiTest extends IntegrationEnvironment {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DealService dealService;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private UserApiClient userApiClient;

    @Autowired
    private UserSecurityContextHolder contextHolder;

    private static final long UNKNOWN_ID = 9999999L;

    @BeforeEach
    void setup() {
        when(userApiClient.getUserById(
                anyLong(),
                anyString(),
                anyString()
        )).thenAnswer(invocationOnMock -> {
            Long uid = invocationOnMock.getArgument(0);
            return testUtils.USERS_MAP.get(uid);
        });
    }


    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createDeal__validDeal_dealCreatedSuccessfully() {
        // Логика тестирования создания сделки
        UserResponseDto buyer = testUtils.BUYER;

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
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(dealCreateRequestDto.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(dealCreateRequestDto.getListingId());
        assertThat(dealResponseDto.getBuyer().getId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(DealStatusDto.PENDING);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createDeal__dealAlreadyExists_errorReturned() {
        // Логика тестирования попытки создания сделки, которая уже существует для данного listing_id
        UserResponseDto buyer = testUtils.BUYER;
        Deal deal = testUtils.createPendingDeal(buyer.getId());
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
                                .header("X-User-Role", buyer.getRole().toString())
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
        UserResponseDto buyer = testUtils.BUYER;
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
                                .header("X-User-Role", buyer.getRole().toString())
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
        UserResponseDto seller = testUtils.SELLER;
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
                                .header("X-User-Role", seller.getRole().toString())
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
        UserResponseDto seller = testUtils.BUYER;

        DealCreateRequestDto dealCreateRequestDto = DealCreateRequestDto.builder()
                .totalPrice(BigDecimal.valueOf(42))
                .listingId(UNKNOWN_ID)
                .build();

        mockMvc.perform(
                        post("/api/v1/deals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", seller.getId())
                                .header("X-User-Role", seller.getRole().toString())
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

        UserResponseDto buyer = testUtils.BUYER;
        Deal pendingDeal = testUtils.createPendingDeal(buyer.getId());
        Deal completedDeal = testUtils.createCompletedDeal(buyer.getId());

        String content = mockMvc.perform(
                        get("/api/v1/deals?status={status}", DealStatus.PENDING)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<DealResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<DealResponseDto>>() {
                }
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
        UserResponseDto buyer = testUtils.BUYER;
        Deal pendingDeal = testUtils.createPendingDeal(buyer.getId());

        String content = mockMvc.perform(
                        get("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getId()).isEqualTo(pendingDeal.getId());
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyer().getId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getCreatedAt()).isEqualTo(pendingDeal.getCreatedAt());
        assertThat(dealResponseDto.getStatus()).isEqualTo(DealStatusDto.PENDING);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealById__notPartyToDeal_errorReturned() {
        // Логика тестирования попытки получения сделки, когда пользователь не является ни покупателем, ни продавцом
        UserResponseDto buyer = testUtils.BUYER;
        Deal pendingDeal = testUtils.createPendingDeal(buyer.getId());
        UserResponseDto anotherBuyer = testUtils.BUYER2;

        mockMvc.perform(
                        get("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", anotherBuyer.getId())
                                .header("X-User-Role", anotherBuyer.getRole().toString())

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
        UserResponseDto buyer = testUtils.BUYER;

        mockMvc.perform(
                        get("/api/v1/deals/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
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
        UserResponseDto buyer = testUtils.BUYER;
        Deal pendingDeal = testUtils.createPendingDeal(buyer.getId());

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.COMPLETED)
                .build();

        String content = mockMvc.perform(
                        put("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyer().getId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(dealCreateRequestDto.getStatus());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void updateDealStatus__setCanceledByBuyer_statusUpdatedSuccessfully() {
        // Логика тестирования обновления статуса сделки по ID
        UserResponseDto buyer = testUtils.BUYER;
        Deal pendingDeal = testUtils.createPendingDeal(buyer.getId());

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        String content = mockMvc.perform(
                        put("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyer().getId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(dealCreateRequestDto.getStatus());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void updateDealStatus__setCanceledBySeller_statusUpdatedSuccessfully() {
        // Логика тестирования обновления статуса сделки по ID
        UserResponseDto buyer = testUtils.BUYER;
        UserResponseDto seller = testUtils.SELLER;
        Deal pendingDeal = testUtils.createPendingDeal(buyer.getId(), seller.getId());

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        String content = mockMvc.perform(
                        put("/api/v1/deals/{id}", pendingDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", seller.getId())
                                .header("X-User-Role", seller.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        DealResponseDto dealResponseDto = objectMapper.readValue(content, DealResponseDto.class);
        assertThat(dealResponseDto.getTotalPrice()).isEqualTo(pendingDeal.getTotalPrice());
        assertThat(dealResponseDto.getListing().getId()).isEqualTo(pendingDeal.getListing().getId());
        assertThat(dealResponseDto.getBuyer().getId()).isEqualTo(buyer.getId());
        assertThat(dealResponseDto.getStatus()).isEqualTo(dealCreateRequestDto.getStatus());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void updateDealStatus__invalidStatusTransitionToPending_errorReturned() {
        // Логика тестирования попытки обновления статуса сделки с неверным переходом
        UserResponseDto buyer = testUtils.BUYER;
        Deal completedDeal = testUtils.createCompletedDeal(buyer.getId());

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.PENDING)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", completedDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())

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
        UserResponseDto buyer = testUtils.BUYER;
        Deal completedDeal = testUtils.createCompletedDeal(buyer.getId());

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", completedDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
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
        UserResponseDto buyer = testUtils.BUYER;
        Deal completedDeal = testUtils.createPendingDeal(buyer.getId());
        UserResponseDto anotherBuyer = testUtils.BUYER2;

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", completedDeal.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", anotherBuyer.getId())
                                .header("X-User-Role", anotherBuyer.getRole().toString())
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
        UserResponseDto buyer = testUtils.BUYER;

        DealStatusUpdateRequestDto dealCreateRequestDto = DealStatusUpdateRequestDto.builder()
                .status(DealStatusDto.CANCELED)
                .build();

        mockMvc.perform(
                        put("/api/v1/deals/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dealCreateRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealByIds__validIds_dealsRetrievedSuccessfully() {
        UserResponseDto buyer = testUtils.BUYER;

        List<Deal> deals = List.of(
                testUtils.createPendingDeal(testUtils.BUYER.getId()),
                testUtils.createPendingDeal(testUtils.BUYER.getId())
        );

        String responseContent = mockMvc.perform(
                        get("/api/v1/deals/in")
                                .param("ids", deals.stream().map(deal -> String.valueOf(deal.getId())).toArray(String[]::new))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<DealResponseDto> response = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertThat(response).hasSize(deals.size());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getDealByIds__invalidId_emptyListReturned() {
        UserResponseDto buyer = testUtils.BUYER;

        String responseContent = mockMvc.perform(
                        get("/api/v1/deals/in")
                                .param("ids", String.valueOf(UNKNOWN_ID))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<DealResponseDto> response = objectMapper.readValue(responseContent, new TypeReference<>() {});
        assertThat(response).isEmpty();
    }
}
