package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class AchievementId {
    @Column(name = "key")
    private String key;

    @Column(name = "game_id")
    private UUID gameId;

    protected AchievementId() {}

    public AchievementId(final String key, final UUID gameId) {
        this.key = key;
        this.gameId = gameId;
    }

    public String key() {
        return key;
    }

    public UUID gameId() {
        return gameId;
    }
}
