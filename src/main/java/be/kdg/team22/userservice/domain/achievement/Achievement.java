package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.achievement.exceptions.AchievementException;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.UUID;

@AggregateRoot
public record Achievement(AchievementId id, ProfileId profileId, UUID gameId, AchievementCode code,
                          Instant unlockedAt) {

    public Achievement {
        if (profileId == null) throw AchievementException.missingProfileId();
        if (code == null) throw AchievementException.missingAchievementCode();
        if (unlockedAt == null) throw AchievementException.missingUnlockedAt();
    }
}