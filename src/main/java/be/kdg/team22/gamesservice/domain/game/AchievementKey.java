package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidAchievementKeyException;

import java.util.regex.Pattern;

public record AchievementKey(String key) {
    private static final Pattern KEY_PATTERN = Pattern.compile("^(?!_)(?!en_)[A-Za-z0-9_]+$", Pattern.CASE_INSENSITIVE);

    public AchievementKey {
        if (key == null || key.isEmpty()) throw new InvalidAchievementKeyException();
        if (!KEY_PATTERN.matcher(key).matches()) throw new InvalidAchievementKeyException(key);

        key = key.toUpperCase();
    }

    public static AchievementKey fromString(String key) {
        return new AchievementKey(key);
    }
}
