package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Setter
public class ApiErrorDto {

    private String code;

    private String message;

    @NotNull
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @NotNull
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

}

