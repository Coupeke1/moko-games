package be.kdg.team22.userservice.domain.achievement;

import java.util.Map;

public class AchievementMetadata {
    private static final Map<String, String> ACHIEVEMENT_NAMES = Map.of(
            "TICTACTOE_DRAW", "Stalemate Master",
            "TICTACTOE_WIN", "Victory Royale",
            "CHECKERS_DRAW", "Unstoppable Force, Immovable Object",
            "CHECKERS_LOSS", "Checker'd Out",
            "CHECKERS_WIN", "Board Dominator",
            "CHECKERS_PROMOTION", "Long Live The King",
            "CHECKERS_MULTICAPTURE", "Float Like A Butterfly...",
            "CHECKERS_THREE_KINGS", "Three Wise Men"
    );
    private static final Map<String, String> ACHIEVEMENT_DESCRIPTIONS = Map.of(
            "TICTACTOE_DRAW", "Draw a TicTacToe game!",
            "TICTACTOE_WIN", "Win your first TicTacToe game!",
            "CHECKERS_DRAW", "Draw a Checkers game!",
            "CHECKERS_LOSS", "Lose a Checkers game!",
            "CHECKERS_WIN", "Win your first Checkers game!",
            "CHECKERS_PROMOTION", "Promote a Checkers piece to a king!",
            "CHECKERS_MULTICAPTURE", "Make a multicapture in Checkers!",
            "CHECKERS_THREE_KINGS", "Own 3 kings in Checkers!"
            );
    private static final Map<String, Integer> ACHIEVEMENT_LEVELS = Map.of(
            "TICTACTOE_DRAW", 2,
            "TICTACTOE_WIN", 3,
            "CHECKERS_DRAW", 2,
            "CHECKERS_LOSS", 1,
            "CHECKERS_WIN", 3,
            "CHECKERS_PROMOTION", 4,
            "CHECKERS_MULTICAPTURE", 5,
            "CHECKERS_THREE_KINGS", 5
    );

    private AchievementMetadata() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String getName(final String code) {
        return ACHIEVEMENT_NAMES.getOrDefault(code, code);
    }

    public static String getDescription(final String code) {
        return ACHIEVEMENT_DESCRIPTIONS.getOrDefault(code, "Achievement unlocked!");
    }

    public static int getLevels(final String code) {
        return ACHIEVEMENT_LEVELS.getOrDefault(code, 1);
    }
}