package ru.itmo.common.dto.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.common.kafka.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingStatusChangedNotificationDto implements Message {
    private Long creatorId;
    private ListingStatusDto listingStatusDto;
    private String title;
}