package be.kdg.team22.gamesservice.api.game.models;

import be.kdg.team22.gamesservice.domain.game.Game;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GameDetailsModel(
        UUID id,
        String name,
        String title,
        String description,
        BigDecimal price,
        String image,
        String frontendUrl,
        String startEndpoint,
        Instant createdAt,
        Instant updatedAt
) {
    public static GameDetailsModel from(Game game) {
        return new GameDetailsModel(
                game.id().value(),
                game.name(),
                game.title(),
                game.description(),
                game.price(),
                game.image(),
                game.frontendUrl(),
                game.startEndpoint(),
                game.createdAt(),
                game.updatedAt()
        );
    }
}