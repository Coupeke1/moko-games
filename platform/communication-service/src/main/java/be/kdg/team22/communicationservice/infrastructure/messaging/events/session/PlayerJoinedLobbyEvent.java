package be.kdg.team22.communicationservice.infrastructure.messaging.events.session;

import java.util.UUID;

public record PlayerJoinedLobbyEvent(
        UUID lobbyId,
        UUID playerId,
        String playerName,
        UUID hostUserId
) {
}