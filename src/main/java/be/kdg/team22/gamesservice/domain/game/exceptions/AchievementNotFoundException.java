package be.kdg.team22.gamesservice.domain.game.exceptions;
import be.kdg.team22.gamesservice.domain.game.AchievementKey;
import be.kdg.team22.gamesservice.domain.game.GameId;

public class AchievementNotFoundException extends RuntimeException {
    public AchievementNotFoundException(AchievementKey key, GameId id) {
        super(String.format("Achievement with key '%s' and game_id %s was not found", key.key(), id.value()));
    }
}