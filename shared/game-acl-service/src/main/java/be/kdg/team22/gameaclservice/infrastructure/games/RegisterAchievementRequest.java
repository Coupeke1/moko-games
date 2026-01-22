package be.kdg.team22.gameaclservice.infrastructure.games;

import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;

public record RegisterAchievementRequest(
        String key,
        String name,
        String description,
        int levels
) {
    public static RegisterAchievementRequest convert(ChessRegisterEvent.ChessAchievement achievement) {
        return new RegisterAchievementRequest(
                achievement.code(),
                achievementCodeToTitle(achievement.code()),
                achievement.description(),
                2
        );
    }

    private static String achievementCodeToTitle(String code) {
        String[] parts = code.split("_");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            result.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase())
                    .append(" ");
        }

        return result.toString().trim();
    }
}
