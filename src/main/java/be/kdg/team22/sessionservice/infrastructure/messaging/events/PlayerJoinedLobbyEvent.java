package be.kdg.team22.sessionservice.infrastructure.messaging.events;

import java.util.UUID;

public record PlayerJoinedLobbyEvent(
        UUID lobbyId,
        UUID playerId,
        String playerName,
        UUID hostUserId
) {
}