package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GameJpaRepository extends JpaRepository<GameEntity, UUID> {}