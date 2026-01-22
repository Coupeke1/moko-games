package be.kdg.team22.userservice.infrastructure.games;

public record AchievementDetailsResponse(
        String key,
        String name,
        String description,
        int levels
) {
}
