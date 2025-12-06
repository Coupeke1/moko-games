package be.kdg.team22.communicationservice.infrastructure.messaging.events.store;

import java.math.BigDecimal;

public record OrderCompletedEvent(
        String userId,
        String orderId,
        BigDecimal totalAmount
) {
}