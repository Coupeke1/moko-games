package be.kdg.team22.storeservice.domain.order.events;

import java.util.List;
import java.util.UUID;

public record GamesPurchasedEvent(
        UUID userId,
        List<UUID> gameIds
) {
}