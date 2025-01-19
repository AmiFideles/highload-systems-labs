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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.common.dto.deal.DealConfirmationDto;
import ru.itmo.common.dto.deal.DealCreatedDto;
import ru.itmo.common.dto.listing.ListingRequestDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.common.dto.listing.ListingStatusChangedNotificationDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.kafka.Message;
import ru.itmo.modules.security.UserSecurityContextHolder;
import ru.itmo.service.market.entity.Category;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.service.ListingService;
import ru.itmo.service.market.util.TestUtils;
import ru.itmo.service.user.client.UserApiClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ListingsApiTest extends IntegrationEnvironment {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ListingService listingService;

    @Autowired
    private TestUtils testUtils;

    @MockBean
    private UserApiClient userApiClient;

    @MockBean
    private KafkaTemplate<String, Message> kafkaTemplate;

    private static final long UNKNOWN_ID = 9999999L;
    private static final String listingUpdatedTopic = "listing-status-updated";

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

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();

        when(kafkaTemplate.send(anyString(), any(Message.class))).thenAnswer(unused -> future);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createListing__validListing_listingCreatedSuccessfully() {
        // Логика тестирования создания объявления

        ListingRequestDto requestDto = getListingRequestDto("New listing", "New listing description");

        String content = mockMvc.perform(
                        post("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", testUtils.SELLER.getId().intValue())
                                .header("X-User-Role", testUtils.SELLER.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ListingResponseDto responseDto = objectMapper.readValue(content, ListingResponseDto.class);

        assertThat(responseDto.getUsed()).isEqualTo(requestDto.getUsed());
        assertThat(responseDto.getCreator().getId()).isEqualTo(testUtils.SELLER.getId());
        assertThat(responseDto.getPrice()).isEqualTo(requestDto.getPrice());
        assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(responseDto.getDescription()).isEqualTo(requestDto.getDescription());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getAllListings__twoListingTypes_listingsRetrievedSuccessfully() {
        UserResponseDto buyer = testUtils.BUYER;
        Listing approvedListing = testUtils.createOpenListing();
        Listing openListing = testUtils.createReviewListing();
        String content = mockMvc.perform(
                        get("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .param("is_used", "false")
                                .header("X-User-Id", buyer.getId().intValue())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(contentNode, new TypeReference<List<ListingResponseDto>>() {
        });
        assertThat(responseDto.size()).isEqualTo(4);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getAllListings__filterCategoryId_listingsRetrievedSuccessfully() {
        UserResponseDto buyer = testUtils.BUYER;
        Listing approvedListing = testUtils.createOpenListing();
        List<Long> idList = approvedListing.getCategories().stream().map(Category::getId).toList();
        String content = mockMvc.perform(
                        get("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .param("is_used", "false")
                                .param("categories_in", idList.get(0).toString())
                                .header("X-User-Id", buyer.getId().intValue())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(contentNode, new TypeReference<List<ListingResponseDto>>() {
        });
        System.out.println("ХУЙ" + responseDto.size());
        assertThat(responseDto.size()).isEqualTo(1);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getAllListings__filterCriteriaMaxPrice_listingsRetrievedSuccessfully() {
        // Логика тестирования поиска объявлений с использованием различных критериев
        UserResponseDto buyer = testUtils.BUYER;
        Listing listing42 = testUtils.createOpenListing(BigDecimal.valueOf(42));
        Listing listing9 = testUtils.createOpenListing(BigDecimal.valueOf(9));

        String content = mockMvc.perform(
                        get("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .param("max_price", "10")
                                .header("X-User-Id", buyer.getId().intValue())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<ListingResponseDto>>() {
                }
        );
        assertThat(responseDto.size()).isEqualTo(4);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getAllListings__filterCriteriaMinPrice_listingsRetrievedSuccessfully() {
        // Логика тестирования поиска объявлений с использованием различных критериев
        UserResponseDto buyer = testUtils.BUYER;
        Listing listing42 = testUtils.createOpenListing(BigDecimal.valueOf(42));
        Listing listing9 = testUtils.createOpenListing(BigDecimal.valueOf(9));

        String content = mockMvc.perform(
                        get("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .param("max_price", "10")
                                .header("X-User-Id", buyer.getId().intValue())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<ListingResponseDto>>() {
                }
        );
        assertThat(responseDto.size()).isEqualTo(4);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getListingById__validId_listingRetrievedSuccessfully() {
        // Логика тестирования получения объявления по ID
        UserResponseDto buyer = testUtils.BUYER;

        Listing listing = testUtils.createReviewListing();

        String content = mockMvc.perform(
                        get("/api/v1/listings/{id}", listing.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ListingResponseDto responseDto = objectMapper.readValue(content, ListingResponseDto.class);

        assertThat(responseDto.getUsed()).isEqualTo(listing.isUsed());
        assertThat(responseDto.getCreator().getId()).isEqualTo(listing.getCreatorId());
        assertThat(responseDto.getPrice()).isEqualTo(listing.getPrice());
        assertThat(responseDto.getTitle()).isEqualTo(listing.getTitle());
        assertThat(responseDto.getDescription()).isEqualTo(listing.getDescription());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getListingById__nonExistentId_errorReturned() {
        // Логика тестирования получения ошибки, если объявление с указанным ID не существует

        UserResponseDto buyer = testUtils.BUYER;

        mockMvc.perform(
                        get("/api/v1/listings/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

//    @Test
//    @Rollback
//    @SneakyThrows
//    @Transactional
//    public void putListingById__validId_listingUpdatedSuccessfully() {
//        // Логика тестирования изменения объявления по ID
//        UserResponseDto seller = testUtils.SELLER;
//
//        Listing listing = testUtils.createReviewListing(seller.getId());
//
//        ListingRequestDto requestDto = getListingRequestDto("Updated listing", "Updated listing description");
//
//        String content = mockMvc.perform(
//                        put("/api/v1/listings/{id}", listing.getId())
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(requestDto))
//                                .header("X-User-Id", seller.getId())
//                                .header("X-User-Role", seller.getRole().toString())
//                )
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        ListingResponseDto responseDto = objectMapper.readValue(content, ListingResponseDto.class);
//        assertThat(responseDto.getUsed()).isEqualTo(requestDto.getUsed());
//        assertThat(responseDto.getCreator().getId()).isEqualTo(seller.getId());
//        assertThat(responseDto.getPrice()).isEqualTo(requestDto.getPrice());
//        assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
//        assertThat(responseDto.getDescription()).isEqualTo(requestDto.getDescription());
//        verify(kafkaTemplate).send(eq(listingUpdatedTopic), any(ListingStatusChangedNotificationDto.class));
//
//    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putListingById__notSellerOrForeignListing_errorReturned() {
        // Логика тестирования попытки изменения объявления, если пользователь не владелец или не продавец
        UserResponseDto seller1 = testUtils.BUYER;
        UserResponseDto seller2 = testUtils.BUYER2;

        Listing listing = testUtils.createReviewListing(seller1.getId());

        ListingRequestDto requestDto = getListingRequestDto(
                "Updated listing",
                "Updated listing description"
        );

        mockMvc.perform(
                        put("/api/v1/listings/{id}", listing.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", seller2.getId())
                                .header("X-User-Role", seller2.getRole().toString())

                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putListingById__nonExistentId_errorReturned() {
        // Логика тестирования изменения объявления, если объявление с указанным ID не существует
        UserResponseDto seller = testUtils.SELLER;

        ListingRequestDto requestDto = getListingRequestDto(
                "Updated listing",
                "Updated listing description"
        );

        mockMvc.perform(
                        put("/api/v1/listings/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
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
    public void deleteListingById__validId_listingDeletedSuccessfully() {
        // Логика тестирования удаления объявления по ID
        UserResponseDto seller = testUtils.SELLER;


        Listing listing = testUtils.createReviewListing(seller.getId());

        mockMvc.perform(
                        delete("/api/v1/listings/{id}", listing.getId())
                                .header("X-User-Id", seller.getId())
                                .header("X-User-Role", seller.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteListingById__notSellerOrForeignListing_errorReturned() {
        // Логика тестирования попытки удаления объявления, если пользователь не владелец или не продавец
        UserResponseDto seller1 = testUtils.SELLER;
        Listing listing = testUtils.createReviewListing(seller1.getId());

        UserResponseDto seller2 = testUtils.SELLER2;

        mockMvc.perform(
                        delete("/api/v1/listings/{id}", listing.getId())
                                .header("X-User-Id", seller2.getId())
                                .header("X-User-Role", seller2.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteListingById__nonExistentId_errorReturned() {
        // Логика тестирования удаления объявления, если объявление с указанным ID не существует
        UserResponseDto seller = testUtils.SELLER;


        mockMvc.perform(
                        delete("/api/v1/listings/{id}", UNKNOWN_ID)
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
    public void getOpenListings__validModeratorOrAdmin_userCanAccess() {
        // Логика тестирования получения открытых объявлений для пользователей с ролью MODERATOR или ADMIN
        UserResponseDto moderator = testUtils.MODERATOR;
        Pageable pageable = PageRequest.of(0, 10);

        testUtils.createOpenListing();

        String content = mockMvc.perform(
                        get("/api/v1/listings/open")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", moderator.getId())
                                .header("X-User-Role", moderator.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())  // Проверка статуса 200 OK
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(contentNode, new TypeReference<List<ListingResponseDto>>() {});
        assertThat(responseDto).isNotEmpty();
    }

    private ListingRequestDto getListingRequestDto(String title, String description) {
        ListingRequestDto requestDto = ListingRequestDto.builder()
                .title(title)
                .description(description)
                .price(BigDecimal.valueOf(42))
                .categoryIds(List.of(
                        testUtils.createCategory().getId(),
                        testUtils.createCategory().getId(),
                        testUtils.createCategory().getId()
                ))
                .used(false)
                .build();
        return requestDto;
    }
}
