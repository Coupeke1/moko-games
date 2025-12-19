package be.kdg.team22.gameaclservice.infrastructure.games;

public record AchievementDetailsResponse(
        String key,
        String name,
        String description,
        int levels
) {
}
