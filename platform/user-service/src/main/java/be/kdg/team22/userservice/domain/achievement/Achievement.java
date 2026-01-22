package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.achievement.exceptions.AchievementException;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Achievement {
    private ProfileId profileId;
    private GameId gameId;
    private AchievementKey key;
    private Instant unlockedAt;

    public Achievement(final ProfileId profileId, final GameId gameId, final AchievementKey key, final Instant unlockedAt) {
        validate(profileId, gameId, key, unlockedAt);
        this.profileId = profileId;
        this.gameId = gameId;
        this.key = key;
        this.unlockedAt = unlockedAt;
    }

    public static Achievement create(final ProfileId profileId, final GameId gameId, final AchievementKey key) {
        return new Achievement(profileId, gameId, key, Instant.now());
    }

    private void validate(final ProfileId profileId, final GameId gameId, final AchievementKey key, final Instant unlockedAt) {
        if (profileId == null) throw AchievementException.missingProfileId();
        if (gameId == null) throw AchievementException.missingGameId();
        if (key == null) throw AchievementException.missingAchievementCode();
        if (unlockedAt == null) throw AchievementException.missingUnlockedAt();
    }

    public ProfileId profileId() {
        return profileId;
    }

    public GameId gameId() {
        return gameId;
    }

    public AchievementKey key() {
        return key;
    }

    public Instant unlockedAt() {
        return unlockedAt;
    }
}