package be.kdg.team22.tictactoeservice.infrastructure.register;

public record RegisterAchievementRequest(
        String key,
        String name,
        String description,
        int levels
) {
}
