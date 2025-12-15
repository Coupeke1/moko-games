package be.kdg.team22.checkersservice.infrastructure.register;

public record RegisterAchievementRequest(
        String key,
        String name,
        String description,
        int levels
) {
}
