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
public class DealConfirmationDto implements Message {
    private Long buyerId;
    private DealStatusDto dealStatusDto;
    private String sellerName;
    private String listingTitle;
}
