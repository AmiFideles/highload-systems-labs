package ru.itmo.common.dto.user;

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
public class ValidateTokenResponseDto {

    @NotNull
    @JsonProperty("user_id")
    private Long userId;

    @NotNull
    @JsonProperty("role")
    private String userRoleDto;

}
