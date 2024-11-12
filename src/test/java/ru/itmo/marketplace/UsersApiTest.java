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
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.model.DealResponseDto;
import ru.itmo.marketplace.model.ListingResponseDto;
import ru.itmo.marketplace.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.itmo.marketplace.TestCheckUtils.UNKNOWN_ID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsersApiTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private TestUtils testUtils;

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getUserListings__validStatus_listingsRetrievedSuccessfully() {
        // Логика тестирования получения моих объявлений с корректным статусом
        User seller = testUtils.createSeller();
        Listing reviewListing = testUtils.createReviewListing(seller.getId());
        Listing openListing = testUtils.createOpenListing(seller);

        String content = mockMvc.perform(
                        get("/api/v1/users/listings?status={listing_status}", reviewListing.getStatus())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", seller.getId())
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
        assertThat(responseDto.size()).isEqualTo(1);
        TestCheckUtils.checkListing(responseDto.get(0), reviewListing);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getUserListings__invalidListingStatus_errorReturned() {
        // Логика тестирования получения ошибки при некорректном статусе объявлений
        User seller = testUtils.createSeller();

        mockMvc.perform(
                        get("/api/v1/users/listings?status={listing_status}", "UNKNOWN")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", seller.getId())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getUserListingsByUserId__validStatus_userListingsRetrievedSuccessfully() {
        // Логика тестирования получения объявлений другого пользователя с корректным статусом
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();
        Listing openListing = testUtils.createReviewListing(seller.getId());
        testUtils.createReviewListing(seller.getId());
        testUtils.createReviewListing(seller.getId());

        String content = mockMvc.perform(
                        get("/api/v1/users/listings?user_id={user_id}", seller.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId())
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
        assertThat(responseDto.size()).isEqualTo(3);
        TestCheckUtils.checkListing(responseDto.get(0), openListing);
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getUserListingsByUserId__invalidListingStatus_errorReturned() {
        // Логика тестирования получения ошибки при некорректном статусе объявлений другого пользователя
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();

        mockMvc.perform(
                        get("/api/v1/users//listings?user_id={user_id}&status={listing_status}",
                                seller.getId(), "UNKNOWN")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getUserListingsByUserId__nonExistentUser_errorReturned() {
        // Логика тестирования получения объявлений для несуществующего пользователя
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/users/listings?user_id={user_id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getUserDealsByUserId__validStatus_userDealsRetrievedSuccessfully() {
        // Логика тестирования получения сделок другого пользователя с корректным статусом
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();
        User moderator = testUtils.createBuyer();

        Deal pendingDeal = testUtils.createPendingDeal(buyer);
        Deal completedDeal = testUtils.createCompletedDeal(buyer);

        String content = mockMvc.perform(
                        get(
                                "/api/v1/users/{user_id}/deals?status={deal_status}",
                                buyer.getId(),
                                pendingDeal.getStatus()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", moderator.getId())
                )
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
    public void getUserDealsByUserId__invalidDealStatus_errorReturned() {
        // Логика тестирования получения ошибки при некорректном статусе сделок другого пользователя
        User buyer = testUtils.createBuyer();
        User seller = testUtils.createSeller();
        User moderator = testUtils.createBuyer();

        Deal pendingDeal = testUtils.createPendingDeal(buyer);
        Deal completedDeal = testUtils.createCompletedDeal(buyer);

        mockMvc.perform(
                        get(
                                "/api/v1/users/{user_id}/deals?status={deal_status}",
                                buyer.getId(),
                                "UNKNOWN"
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", moderator.getId())
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getUserDealsByUserId__nonExistentUser_errorReturned() {
        // Логика тестирования получения сделок для несуществующего пользователя
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/users/{user_id}/deals", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId())
                )
                .andExpect(status().isNotFound());
    }

}
