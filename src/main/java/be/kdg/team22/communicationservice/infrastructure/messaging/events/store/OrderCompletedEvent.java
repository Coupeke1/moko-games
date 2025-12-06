package be.kdg.team22.communicationservice.infrastructure.messaging.events.store;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCompletedEvent(
        UUID userId,
        UUID orderId,
        BigDecimal totalAmount
) {
}