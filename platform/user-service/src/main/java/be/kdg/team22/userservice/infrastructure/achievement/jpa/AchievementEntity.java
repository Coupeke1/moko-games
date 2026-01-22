package be.kdg.team22.userservice.infrastructure.achievement.jpa;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementKey;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "achievements")
public class AchievementEntity {
    @EmbeddedId
    private AchievementId id;

    @Column(name = "unlocked_at", nullable = false)
    private Instant unlockedAt;

    protected AchievementEntity() {
    }

    public AchievementEntity(
            String key,
            UUID profileId,
            UUID gameId,
            Instant unlockedAt
    ) {
        this.id = new AchievementId(key, profileId, gameId);
        this.unlockedAt = unlockedAt;
    }

    public static AchievementEntity fromDomain(Achievement achievement) {
        return new AchievementEntity(
                achievement.key().key(),
                achievement.profileId().value(),
                achievement.gameId().value(),
                achievement.unlockedAt()
        );
    }

    public Achievement toDomain() {
        return new Achievement(
                new ProfileId(id.profileId()),
                new GameId(id.gameId()),
                new AchievementKey(id.key()),
                unlockedAt
        );
    }

    public UUID getProfileId() {
        return id.profileId();
    }

    public UUID getGameId() {
        return id.gameId();
    }

    public String getKey() {
        return id.key();
    }

    public Instant getUnlockedAt() {
        return unlockedAt;
    }
}