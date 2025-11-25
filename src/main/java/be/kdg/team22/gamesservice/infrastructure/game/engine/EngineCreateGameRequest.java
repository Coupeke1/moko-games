package be.kdg.team22.gamesservice.infrastructure.game.engine;

import java.util.List;
import java.util.UUID;

public record EngineCreateGameRequest(
        List<UUID> players,
        Object settings
) {
}