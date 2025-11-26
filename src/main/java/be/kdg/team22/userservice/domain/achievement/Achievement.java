package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.UUID;

@AggregateRoot
public record Achievement(AchievementId id, ProfileId profileId, UUID gameId, AchievementCode code,
                          Instant unlockedAt) {

    public Achievement {
        //TODO custom exception
        if (profileId == null) throw new IllegalArgumentException("profileId cannot be null");
        if (code == null) throw new IllegalArgumentException("code cannot be null");
        if (unlockedAt == null) throw new IllegalArgumentException("unlockedAt cannot be null");

    }
}