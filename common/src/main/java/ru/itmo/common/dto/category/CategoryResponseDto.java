package ru.itmo.common.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {

    @NotNull
    @JsonProperty("id")
    private Long id;

    @NotNull
    @JsonProperty("name")
    private String name;

}

