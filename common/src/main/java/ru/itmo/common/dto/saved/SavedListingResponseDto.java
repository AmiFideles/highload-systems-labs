package ru.itmo.common.dto.saved;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.itmo.common.dto.listing.ListingResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedListingResponseDto {

  @NotNull @Valid
  @JsonProperty("listing")
  private ListingResponseDto listing;

  @NotNull @Valid
  @JsonProperty("created_at")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime createdAt;

}

