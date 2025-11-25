package be.kdg.team22.gamesservice.infrastructure.db.repositories;

import be.kdg.team22.gamesservice.infrastructure.db.entities.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaGameRepository extends JpaRepository<GameEntity, UUID> {
}
