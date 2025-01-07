package ru.itmo.common.dto.deal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.common.kafka.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealCreatedDto implements Message {
    private Long sellerId;
    private String buyerName;
    private String listingTitle;
}
