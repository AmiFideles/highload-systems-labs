package ru.itmo.common.dto.listing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.itmo.common.dto.category.CategoryResponseDto;
import ru.itmo.common.dto.user.UserResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingResponseDto {

    @NotNull
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @NotNull
    @Valid
    @JsonProperty("price")
    private BigDecimal price;

    @Valid
    @JsonProperty("categories")
    private List<@Valid CategoryResponseDto> categories = new ArrayList<>();

    @NotNull
    @JsonProperty("creator")
    private UserResponseDto creator;

    @Valid
    @JsonProperty("created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @Valid
    @JsonProperty("updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    @NotNull
    @JsonProperty("used")
    private Boolean used;

    @JsonProperty("image_file_name")
    private String imageFileName;

}

