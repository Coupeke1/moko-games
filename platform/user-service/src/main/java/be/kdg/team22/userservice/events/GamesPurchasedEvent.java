package be.kdg.team22.userservice.events;

import java.util.List;
import java.util.UUID;

public record GamesPurchasedEvent(UUID userId, List<UUID> gameIds) {
}

