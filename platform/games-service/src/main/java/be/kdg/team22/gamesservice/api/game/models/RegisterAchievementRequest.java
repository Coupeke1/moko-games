package be.kdg.team22.gamesservice.api.game.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RegisterAchievementRequest(
        @NotNull String key,
        @NotNull String name,
        @NotNull String description,
        @NotNull @PositiveOrZero int levels
        ) {
}
