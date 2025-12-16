package be.kdg.team22.sessionservice.infrastructure.games;

import java.util.Map;
import java.util.UUID;

public record StartGameRequest(
        UUID lobbyId,
        UUID gameId,
        java.util.List<UUID> players,
        Map<String, Object> settings,
        boolean aiPlayer
) {
}