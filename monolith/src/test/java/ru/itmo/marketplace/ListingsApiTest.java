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
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.dto.ListingRequestDto;
import ru.itmo.marketplace.dto.ListingResponseDto;
import ru.itmo.marketplace.service.ListingService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ListingsApiTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ListingService listingService;

    @Autowired
    private TestUtils testUtils;

    private static final long UNKNOWN_ID = 9999999L;

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void createListing__validListing_listingCreatedSuccessfully() {
        // Логика тестирования создания объявления
        User seller = testUtils.createSeller();

        ListingRequestDto requestDto = getListingRequestDto("New listing", "New listing description");

        String content = mockMvc.perform(
                        post("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", seller.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ListingResponseDto responseDto = objectMapper.readValue(content, ListingResponseDto.class);

        assertThat(responseDto.getUsed()).isEqualTo(requestDto.getUsed());
        assertThat(responseDto.getCreatorId()).isEqualTo(seller.getId());
        assertThat(responseDto.getPrice()).isEqualTo(requestDto.getPrice());
        assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(responseDto.getDescription()).isEqualTo(requestDto.getDescription());
    }


    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getAllListings__twoListingTypes_listingsRetrievedSuccessfully() {
        // Логика тестирования поиска объявлений с использованием различных критериев
        User buyer = testUtils.createBuyer();
        Listing approvedListing = testUtils.createOpenListing();
        Listing openListing = testUtils.createReviewListing();

        String content = mockMvc.perform(
                        get("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId().intValue())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<ListingResponseDto>>() {}
        );
        assertThat(responseDto.size()).isEqualTo(1);
        ListingResponseDto listingResponseDto = responseDto.get(0);
        TestCheckUtils.checkListing(listingResponseDto, approvedListing);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getAllListings__filterCriteriaMaxPrice_listingsRetrievedSuccessfully() {
        // Логика тестирования поиска объявлений с использованием различных критериев
        User buyer = testUtils.createBuyer();
        Listing listing42 = testUtils.createOpenListing(BigDecimal.valueOf(42));
        Listing listing9 = testUtils.createOpenListing(BigDecimal.valueOf(9));

        String content = mockMvc.perform(
                        get("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .param("max_price", "10")
                                .header("X-User-Id", buyer.getId().intValue())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<ListingResponseDto>>() {}
        );
        assertThat(responseDto.size()).isEqualTo(1);
        ListingResponseDto listingResponseDto = responseDto.get(0);
        TestCheckUtils.checkListing(listingResponseDto, listing9);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getAllListings__filterCriteriaMinPrice_listingsRetrievedSuccessfully() {
        // Логика тестирования поиска объявлений с использованием различных критериев
        User buyer = testUtils.createBuyer();
        Listing listing42 = testUtils.createOpenListing(BigDecimal.valueOf(42));
        Listing listing9 = testUtils.createOpenListing(BigDecimal.valueOf(9));

        String content = mockMvc.perform(
                        get("/api/v1/listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .param("max_price", "10")
                                .header("X-User-Id", buyer.getId().intValue())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
        JsonNode contentNode = jsonNode.get("content");
        List<ListingResponseDto> responseDto = objectMapper.convertValue(
                contentNode,
                new TypeReference<List<ListingResponseDto>>() {}
        );
        assertThat(responseDto.size()).isEqualTo(1);
        ListingResponseDto listingResponseDto = responseDto.get(0);
        TestCheckUtils.checkListing(listingResponseDto, listing9);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getListingById__validId_listingRetrievedSuccessfully() {
        // Логика тестирования получения объявления по ID
        User buyer = testUtils.createBuyer();

        Listing listing = testUtils.createReviewListing();

        String content = mockMvc.perform(
                        get("/api/v1/listings/{id}", listing.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ListingResponseDto responseDto = objectMapper.readValue(content, ListingResponseDto.class);

        assertThat(responseDto.getUsed()).isEqualTo(listing.isUsed());
        assertThat(responseDto.getCreatorId()).isEqualTo(listing.getCreator().getId());
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

        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/listings/{id}", UNKNOWN_ID)
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
    public void putListingById__validId_listingUpdatedSuccessfully() {
        // Логика тестирования изменения объявления по ID
        User seller = testUtils.createBuyer();

        Listing listing = testUtils.createReviewListing(seller.getId());

        ListingRequestDto requestDto = getListingRequestDto("Updated listing", "Updated listing description");

        String content = mockMvc.perform(
                        put("/api/v1/listings/{id}", listing.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", seller.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ListingResponseDto responseDto = objectMapper.readValue(content, ListingResponseDto.class);
        assertThat(responseDto.getUsed()).isEqualTo(requestDto.getUsed());
        assertThat(responseDto.getCreatorId()).isEqualTo(seller.getId());
        assertThat(responseDto.getPrice()).isEqualTo(requestDto.getPrice());
        assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(responseDto.getDescription()).isEqualTo(requestDto.getDescription());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void putListingById__notSellerOrForeignListing_errorReturned() {
        // Логика тестирования попытки изменения объявления, если пользователь не владелец или не продавец
        User seller1 = testUtils.createBuyer();
        User seller2 = testUtils.createBuyer();

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
        User seller = testUtils.createBuyer();

        ListingRequestDto requestDto = getListingRequestDto(
                "Updated listing",
                "Updated listing description"
        );

        mockMvc.perform(
                        put("/api/v1/listings/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", seller.getId())
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
        User seller = testUtils.createSeller();

        Listing listing = testUtils.createReviewListing(seller.getId());

        mockMvc.perform(
                        delete("/api/v1/listings/{id}", listing.getId())
                                .header("X-User-Id", seller.getId())
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
        User seller1 = testUtils.createSeller();
        Listing listing = testUtils.createReviewListing(seller1.getId());

        User seller2 = testUtils.createSeller();

        mockMvc.perform(
                        delete("/api/v1/listings/{id}", listing.getId())
                                .header("X-User-Id", seller2.getId())
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
        User seller = testUtils.createSeller();

        mockMvc.perform(
                        delete("/api/v1/listings/{id}", UNKNOWN_ID)
                                .header("X-User-Id", seller.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
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
