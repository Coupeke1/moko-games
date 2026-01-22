package be.kdg.team22.userservice.domain.achievement;

import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AchievementRepository {
    void save(Achievement achievement);

    Optional<Achievement> findByProfileGameAndKey(ProfileId profileId, GameId gameId, AchievementKey key);

    List<Achievement> findByProfile(ProfileId profileId);

    boolean existsByProfileGameAndKey(ProfileId profileId, GameId gameId, AchievementKey key);

    long countByProfile(ProfileId profileId);

    long countByProfileAndGame(ProfileId profileId, UUID gameId);
}