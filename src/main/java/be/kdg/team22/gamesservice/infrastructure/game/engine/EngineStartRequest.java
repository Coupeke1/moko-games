package be.kdg.team22.gamesservice.infrastructure.game.engine;

import java.util.List;
import java.util.UUID;

public record EngineStartRequest(
        UUID templateId,
        UUID lobbyId,
        List<UUID> players,
        Object settings
) {
}