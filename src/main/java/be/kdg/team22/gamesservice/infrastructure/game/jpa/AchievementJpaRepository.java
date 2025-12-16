package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AchievementJpaRepository extends JpaRepository<AchievementEntity, AchievementId> {
    Optional<AchievementEntity> findById(AchievementId id);
}
