package be.kdg.team22.userservice.infrastructure.achievement.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaAchievementRepository extends JpaRepository<AchievementEntity, UUID> {

    Optional<AchievementEntity> findByProfileIdAndCode(UUID profileId, String code);

    List<AchievementEntity> findByProfileId(UUID profileId);

    boolean existsByProfileIdAndCode(UUID profileId, String code);

    long countByProfileId(UUID profileId);

    long countByProfileIdAndGameId(UUID profileId, UUID gameId);
}