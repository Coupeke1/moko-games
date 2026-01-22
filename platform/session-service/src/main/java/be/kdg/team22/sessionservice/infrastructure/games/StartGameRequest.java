package be.kdg.team22.sessionservice.infrastructure.games;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record StartGameRequest(
        UUID lobbyId,
        UUID gameId,
        List<UUID> players,
        boolean hasBot,
        Map<String, Object> settings
) {
}