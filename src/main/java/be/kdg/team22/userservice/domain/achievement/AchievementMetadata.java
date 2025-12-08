package be.kdg.team22.userservice.domain.achievement;

import java.util.Map;

public class AchievementMetadata {
    private static final Map<String, String> ACHIEVEMENT_NAMES = Map.of(
            "TICTACTOE_WIN", "Victory Royale",
            "TICTACTOE_DRAW", "Stalemate Master"
    );
    private static final Map<String, String> ACHIEVEMENT_DESCRIPTIONS = Map.of(
            "TICTACTOE_WIN", "Win your first TicTacToe game!",
            "TICTACTOE_DRAW", "Draw a TicTacToe game!"
    );

    private AchievementMetadata() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String getName(String code) {
        return ACHIEVEMENT_NAMES.getOrDefault(code, code);
    }

    public static String getDescription(String code) {
        return ACHIEVEMENT_DESCRIPTIONS.getOrDefault(code, "Achievement unlocked!");
    }
}