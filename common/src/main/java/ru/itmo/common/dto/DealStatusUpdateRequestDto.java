package ru.itmo.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DealStatusUpdateRequestDto {

    @NotNull
    @Valid
    @JsonProperty("status")
    private DealStatusDto status;

}

