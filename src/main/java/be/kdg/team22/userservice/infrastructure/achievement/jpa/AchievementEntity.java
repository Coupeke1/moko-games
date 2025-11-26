package be.kdg.team22.userservice.infrastructure.achievement.jpa;

import be.kdg.team22.userservice.domain.achievement.Achievement;
import be.kdg.team22.userservice.domain.achievement.AchievementCode;
import be.kdg.team22.userservice.domain.achievement.AchievementId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "achievements")
public class AchievementEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Column(name = "game_id")
    private UUID gameId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "unlocked_at", nullable = false)
    private Instant unlockedAt;

    protected AchievementEntity() {
    }

    public AchievementEntity(
            UUID id,
            UUID profileId,
            UUID gameId,
            String code,
            Instant unlockedAt
    ) {
        this.id = id;
        this.profileId = profileId;
        this.gameId = gameId;
        this.code = code;
        this.unlockedAt = unlockedAt;
    }

    public static AchievementEntity fromDomain(Achievement achievement) {
        return new AchievementEntity(
                achievement.id().value(),
                achievement.profileId().value(),
                achievement.gameId(),
                achievement.code().value(),
                achievement.unlockedAt()
        );
    }

    public Achievement toDomain() {
        return new Achievement(
                new AchievementId(id),
                new ProfileId(profileId),
                gameId,
                new AchievementCode(code),
                unlockedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public UUID getGameId() {
        return gameId;
    }

    public String getCode() {
        return code;
    }

    public Instant getUnlockedAt() {
        return unlockedAt;
    }
}