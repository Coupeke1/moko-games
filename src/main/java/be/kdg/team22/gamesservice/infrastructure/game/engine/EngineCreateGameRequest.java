package be.kdg.team22.gamesservice.infrastructure.game.engine;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record EngineCreateGameRequest(
        List<UUID> players,
        Map<String, Object> settings,
        boolean aiPlayer
) {
}