package be.kdg.team22.communicationservice.infrastructure.messaging.events.achievements;

public record AchievementUnlockedEvent(
        String playerId,
        String achievementId,
        String achievementName,
        String description
) {
}