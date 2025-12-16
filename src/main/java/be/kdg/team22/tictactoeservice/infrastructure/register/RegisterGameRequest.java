package be.kdg.team22.tictactoeservice.infrastructure.register;

import java.math.BigDecimal;
import java.util.List;

public record RegisterGameRequest(
        String name,
        String backendUrl,
        String frontendUrl,
        String startEndpoint,
        String healthEndpoint,

        String title,
        String description,
        String image,
        BigDecimal price,
        String category,

        GameSettingsDefinition settingsDefinition,
        List<RegisterAchievementRequest> achievements
) {
    public record GameSettingsDefinition(List<RegisterSettingsRequest> settings) {}
}
