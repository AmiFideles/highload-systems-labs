package ru.itmo.marketplace.service.image.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.marketplace.service.image.IntegrationEnvironment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ImagesControllerTest extends IntegrationEnvironment {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @SneakyThrows
    void testDownloadImageNotFound() {
        mockMvc.perform(
                        get("/api/v1/images/1")
                                .header("X-User-Id", 1L)
                                .header("X-User-Role", "BUYER")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testUploadImage() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/v1/images")
                                .file(file)
                                .header("X-User-Id", 1L)
                                .header("X-User-Role", "BUYER")
                )
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testDownloadImageSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        String filename = mockMvc.perform(
                        multipart("/api/v1/images")
                                .file(file)
                                .header("X-User-Id", 1L)
                                .header("X-User-Role", "BUYER")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(
                        get("/api/v1/images/" + filename)
                                .header("X-User-Id", 1L)
                                .header("X-User-Role", "BUYER")
                )
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testDeleteImageSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes()
        );

        String filename = mockMvc.perform(
                        multipart("/api/v1/images")
                                .file(file)
                                .header("X-User-Id", 1L)
                                .header("X-User-Role", "BUYER")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(
                        delete("/api/v1/images/" + filename)
                                .header("X-User-Id", 1L)
                                .header("X-User-Role", "BUYER")
                )
                .andExpect(status().isOk());
    }

}
