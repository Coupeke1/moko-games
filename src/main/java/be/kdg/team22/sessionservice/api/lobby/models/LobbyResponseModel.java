package be.kdg.team22.sessionservice.api.lobby.models;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record LobbyResponseModel(
        UUID id,
        UUID gameId,
        UUID ownerId,
        Set<UUID> players,
        String status,
        Instant createdAt
) {
}