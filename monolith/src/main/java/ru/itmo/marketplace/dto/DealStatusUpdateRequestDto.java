package ru.itmo.marketplace.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class DealStatusUpdateRequestDto {

    private DealStatusDto status;

    public DealStatusUpdateRequestDto status(DealStatusDto status) {
        this.status = status;
        return this;
    }

    @NotNull
    @Valid
    @JsonProperty("status")
    public DealStatusDto getStatus() {
        return status;
    }

    public void setStatus(DealStatusDto status) {
        this.status = status;
    }

}

