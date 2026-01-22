package be.kdg.team22.userservice.infrastructure.achievement.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaAchievementRepository extends JpaRepository<AchievementEntity, AchievementId> {

    Optional<AchievementEntity> findByIdProfileIdAndIdGameIdAndIdKey(UUID profileId, UUID gameId, String key);

    List<AchievementEntity> findByIdProfileId(UUID profileId);

    boolean existsByIdProfileIdAndIdGameIdAndIdKey(UUID profileId, UUID gameId, String key);

    long countByIdProfileId(UUID profileId);


    long countByIdProfileIdAndIdGameId(UUID profileId, UUID gameId);
}