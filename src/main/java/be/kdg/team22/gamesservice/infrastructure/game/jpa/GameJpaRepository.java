package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GameJpaRepository extends JpaRepository<GameEntity, UUID> {
    Optional<GameEntity> findByName(String name);
}