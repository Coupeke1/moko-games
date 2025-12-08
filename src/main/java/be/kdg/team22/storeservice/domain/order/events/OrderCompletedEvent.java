package be.kdg.team22.storeservice.domain.order.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCompletedEvent(
        UUID userId,
        UUID orderId,
        BigDecimal totalAmount
) {
}