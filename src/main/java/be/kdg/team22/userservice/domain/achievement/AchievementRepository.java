package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementRepository {
    void save(Achievement achievement);

    Optional<Achievement> findByProfileAndCode(ProfileId profileId, AchievementCode code);

    List<Achievement> findByProfile(ProfileId profileId);

    boolean existsByProfileAndCode(ProfileId profileId, AchievementCode code);

    long countByProfile(ProfileId profileId);

    long countByProfileAndGame(ProfileId profileId, UUID gameId);
}