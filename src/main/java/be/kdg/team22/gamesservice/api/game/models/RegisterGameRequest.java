package be.kdg.team22.gamesservice.api.game.models;

import be.kdg.team22.gamesservice.domain.game.GameCategory;
import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsDefinition;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record RegisterGameRequest(
        @NotNull String name,
        @NotNull String backendUrl,
        @NotNull String frontendUrl,
        @NotNull String startEndpoint,
        @NotNull String healthEndpoint,

        @NotNull String title,
        @NotNull String description,
        @NotNull String image,
        @NotNull @Positive BigDecimal price,
        @NotNull GameCategory category,
        @NotNull GameSettingsDefinition settingsDefinition,
        List<RegisterAchievementRequest> achievements
) {
}
