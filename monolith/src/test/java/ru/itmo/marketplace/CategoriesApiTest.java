package ru.itmo.marketplace;

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
import ru.itmo.marketplace.entity.Category;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.dto.CategoryRequestDto;
import ru.itmo.marketplace.dto.CategoryResponseDto;
import ru.itmo.marketplace.repository.CategoryRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CategoriesApiTest extends IntegrationEnvironment {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestUtils testUtils;

    private static final long UNKNOWN_ID = 9999999L;

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void createCategory__validNewCategory_returnSuccess() {
        User moderator = testUtils.createModerator();

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("New Category")
                .build();

        String content = mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", moderator.getId().intValue())
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CategoryResponseDto responseDto = objectMapper.readValue(content, CategoryResponseDto.class);

        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void createCategory__alreadyExists_returnError() {
        User moderator = testUtils.createModerator();
        Category category = testUtils.createCategory();

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name(category.getName())
                .build();

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", moderator.getId().intValue())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    public void getCategoryPageable__buyerCall_returnSuccessPageable() {
        User buyer = testUtils.createBuyer();
        Category category = testUtils.createCategory();

        mockMvc.perform(
                        get("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("page", "0")
                                .param("size", "10")
                                .header("X-User-Id", buyer.getId().intValue())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(category.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(category.getName())));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void getCategoryById__categoryExists_returnSuccess() {
        User buyer = testUtils.createBuyer();
        Category category = testUtils.createCategory();

        mockMvc.perform(
                        get("/api/v1/categories/{id}", category.getId())
                                .header("X-User-Id", buyer.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andExpect(jsonPath("$.id").value(category.getId()));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void getCategoryById__categoryDoesNotExists_returnNotFound() {
        User buyer = testUtils.createBuyer();

        mockMvc.perform(
                        get("/api/v1/categories/{id}", UNKNOWN_ID)
                                .header("X-User-Id", buyer.getId())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void updateCategory__categoryExists_updateSuccess() {
        User moderator = testUtils.createModerator();
        Category category = testUtils.createCategory();

        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("Updated Name")
                .build();

        mockMvc.perform(
                        put("/api/v1/categories/{id}", category.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", moderator.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void updateCategory__categoryDoesNotExist_returnNotFound() {
        User moderator = testUtils.createModerator();
        CategoryRequestDto requestDto = CategoryRequestDto.builder()
                .name("Updated Name")
                .build();

        mockMvc.perform(
                        put("/api/v1/categories/{id}", UNKNOWN_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto))
                                .header("X-User-Id", moderator.getId())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void deleteCategory__categoryExists_returnSuccess() {
        User moderator = testUtils.createModerator();
        Category category = testUtils.createCategory();

        mockMvc.perform(
                        delete("/api/v1/categories/{id}", category.getId())
                                .header("X-User-Id", moderator.getId())
                )
                .andExpect(status().isOk());

        Optional<Category> deletedCategory = categoryRepository.findById(category.getId());
        assertThat(deletedCategory).isEmpty();
    }

    @Test
    @Rollback
    @SneakyThrows
    @Transactional
    void deleteCategory__categoryDoesNotExist_returnNotFound() {
        User moderator = testUtils.createModerator();

        mockMvc.perform(
                        delete("/api/v1/categories/{id}", UNKNOWN_ID)
                                .header("X-User-Id", moderator.getId())
                )
                .andExpect(status().isNotFound());
    }
}
