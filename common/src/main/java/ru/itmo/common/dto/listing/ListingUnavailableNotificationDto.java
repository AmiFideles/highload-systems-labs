    package ru.itmo.common.dto.listing;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import ru.itmo.common.kafka.Message;

    import java.util.List;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class ListingUnavailableNotificationDto implements Message {
        List<Long> userId;
        String listingTitle;
    }
