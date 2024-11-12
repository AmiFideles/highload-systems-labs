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
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.SavedListing;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.dto.SavedListingRequestDto;
import ru.itmo.marketplace.dto.SavedListingResponseDto;
import ru.itmo.marketplace.service.SavedListingService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SavedListingsApiTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SavedListingService savedListingService;

    @Autowired
    private TestUtils testUtils;

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postSavedListings__validListingForCurrentUser_listingAddedSuccessfully() {
        Listing listing = testUtils.createReviewListing();
        User buyer = testUtils.createBuyer();

        SavedListingRequestDto savedListingRequestDto = SavedListingRequestDto.builder()
                .listingId(listing.getId())
                .build();

        String content = mockMvc.perform(
                        post("/api/v1/saved-listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedListingRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SavedListingResponseDto responseDto = objectMapper.readValue(content, SavedListingResponseDto.class);
        assertThat(responseDto.getListing().getId()).isEqualTo(listing.getId());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void postSavedListings__listingAlreadySaved_errorReturned() {
        Listing listing = testUtils.createReviewListing();
        User buyer = testUtils.createBuyer();

        SavedListingRequestDto savedListingRequestDto = SavedListingRequestDto.builder()
                .listingId(listing.getId())
                .build();

        // Логика тестирования ситуации, когда объявление уже добавлено в сохраненные
        mockMvc.perform(
                        post("/api/v1/saved-listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedListingRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andExpect(status().isOk());

        // Тест дублирования
        mockMvc.perform(
                        post("/api/v1/saved-listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedListingRequestDto))
                                .header("X-User-Id", buyer.getId())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getPaginatedSavedListings__savedListingsForCurrentUser_listingsRetrievedSuccessfully() {
        // Логика тестирования получения сохраненных объявлений для текущего пользователя
        User buyer = testUtils.createBuyer();
        SavedListing savedListing = testUtils.createSavedListing(buyer.getId());

        mockMvc.perform(
                        get("/api/v1/saved-listings")
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].listing.id", is(savedListing.getListingId().intValue())))
                .andExpect(jsonPath("$.total_elements", is(1)))
                .andExpect(jsonPath("$.total_pages", is(1)));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getSavedListingById__validListingIdForCurrentUser_listingRetrievedSuccessfully() {
        // Логика тестирования получения сохраненного объявления по ID для текущего пользователя
        User buyer = testUtils.createBuyer();
        SavedListing savedListing = testUtils.createSavedListing(buyer.getId());

        mockMvc.perform(
                        get("/api/v1/saved-listings/{listing_id}", savedListing.getListingId().intValue())
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.listing.id", is(savedListing.getListingId().intValue())));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getSavedListingById__nonExistentListingId_errorReturned() {
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/saved-listings/{listing_id}", 123456789)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteSavedListingById__validListingIdForCurrentUser_listingDeletedSuccessfully() {
        // Логика тестирования удаления валидного объявления из сохраненных для текущего пользователя
        User buyer = testUtils.createBuyer();
        SavedListing savedListing = testUtils.createSavedListing(buyer.getId());

        mockMvc.perform(
                        delete("/api/v1/saved-listings/{listing_id}", savedListing.getListingId().intValue())
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void deleteSavedListingById__nonExistentListingId_errorReturned() {
        // Логика тестирования получения ошибки при попытке удалить несуществующее объявление
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        delete("/api/v1/saved-listings/{listing_id}", 123456789)
                                .header("X-User-Id", buyer.getId())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
