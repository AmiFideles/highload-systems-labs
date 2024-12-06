package ru.itmo.common.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    @NotNull
    @JsonProperty("id")
    private Long id;

    @NotNull
    @Email
    @JsonProperty("email")
    private String email;

    @NotNull
    @JsonProperty("name")
    private String name;

    @NotNull
    @JsonProperty("role")
    private UserRoleDto role;

}

