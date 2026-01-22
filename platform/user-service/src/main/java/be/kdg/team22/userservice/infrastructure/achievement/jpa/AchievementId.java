package be.kdg.team22.userservice.infrastructure.achievement.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class AchievementId {
    private String key;
    @Column(name = "profile_id")
    private UUID profileId;
    @Column(name = "game_id")
    private UUID gameId;

    protected AchievementId() {
    }

    public AchievementId(String key, UUID profileId, UUID gameId) {
        this.key = key;
        this.gameId = gameId;
        this.profileId = profileId;
    }

    public String key() {
        return key;
    }

    public UUID gameId() {
        return gameId;
    }

    public UUID profileId() {
        return profileId;
    }
}
