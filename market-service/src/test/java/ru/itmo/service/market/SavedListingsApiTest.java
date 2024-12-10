package ru.itmo.service.market;

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
import ru.itmo.common.dto.saved.SavedListingRequestDto;
import ru.itmo.common.dto.saved.SavedListingResponseDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.modules.security.UserSecurityContextHolder;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.SavedListing;
import ru.itmo.service.market.service.SavedListingService;
import ru.itmo.service.market.util.TestUtils;
import ru.itmo.service.user.client.UserApiClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SavedListingsApiTest extends IntegrationEnvironment {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SavedListingService savedListingService;

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
    public void postSavedListings__validListingForCurrentUser_listingAddedSuccessfully() {
        Listing listing = testUtils.createReviewListing();
        UserResponseDto buyer = testUtils.BUYER;

        SavedListingRequestDto savedListingRequestDto = SavedListingRequestDto.builder()
                .listingId(listing.getId())
                .build();

        String content = mockMvc.perform(
                        post("/api/v1/saved-listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedListingRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
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
        UserResponseDto buyer = testUtils.BUYER;

        SavedListingRequestDto savedListingRequestDto = SavedListingRequestDto.builder()
                .listingId(listing.getId())
                .build();

        // Логика тестирования ситуации, когда объявление уже добавлено в сохраненные
        mockMvc.perform(
                        post("/api/v1/saved-listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedListingRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andExpect(status().isOk());

        // Тест дублирования
        mockMvc.perform(
                        post("/api/v1/saved-listings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedListingRequestDto))
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getPaginatedSavedListings__savedListingsForCurrentUser_listingsRetrievedSuccessfully() {
        // Логика тестирования получения сохраненных объявлений для текущего пользователя
        UserResponseDto buyer = testUtils.BUYER;
        SavedListing savedListing = testUtils.createSavedListing(buyer.getId());

        mockMvc.perform(
                        get("/api/v1/saved-listings")
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].listing.id", is(savedListing.getListingId().intValue())));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getSavedListingById__validListingIdForCurrentUser_listingRetrievedSuccessfully() {
        // Логика тестирования получения сохраненного объявления по ID для текущего пользователя
        UserResponseDto buyer = testUtils.BUYER;
        SavedListing savedListing = testUtils.createSavedListing(buyer.getId());

        mockMvc.perform(
                        get("/api/v1/saved-listings/{listing_id}", savedListing.getListingId().intValue())
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
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
        UserResponseDto buyer = testUtils.BUYER;

        mockMvc.perform(
                        get("/api/v1/saved-listings/{listing_id}", 123456789)
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
    public void deleteSavedListingById__validListingIdForCurrentUser_listingDeletedSuccessfully() {
        // Логика тестирования удаления валидного объявления из сохраненных для текущего пользователя
        UserResponseDto buyer = testUtils.BUYER;
        SavedListing savedListing = testUtils.createSavedListing(buyer.getId());

        mockMvc.perform(
                        delete("/api/v1/saved-listings/{listing_id}", savedListing.getListingId().intValue())
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
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
        UserResponseDto buyer = testUtils.BUYER;

        mockMvc.perform(
                        delete("/api/v1/saved-listings/{listing_id}", 123456789)
                                .header("X-User-Id", buyer.getId())
                                .header("X-User-Role", buyer.getRole().toString())
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
