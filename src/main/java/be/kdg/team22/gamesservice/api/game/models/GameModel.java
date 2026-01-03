package be.kdg.team22.gamesservice.api.game.models;

import be.kdg.team22.gamesservice.domain.game.Game;

import java.time.Instant;
import java.util.UUID;

public record GameModel(
        UUID id,
        String name,
        String title,
        String description,
        String image,
        String frontendUrl,
        String startEndpoint,
        boolean healthy,
        Instant lastHealthCheck,
        Instant createdAt,
        Instant updatedAt
) {
    public static GameModel from(Game game) {
        return new GameModel(
                game.id().value(),
                game.name(),
                game.title(),
                game.description(),
                game.image(),
                game.frontendUrl(),
                game.startEndpoint(),
                game.healthy(),
                game.lastHealthCheck(),
                game.createdAt(),
                game.updatedAt()
        );
    }
}