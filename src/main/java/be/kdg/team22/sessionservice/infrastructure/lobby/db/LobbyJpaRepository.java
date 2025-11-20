package be.kdg.team22.sessionservice.infrastructure.lobby.db;

import be.kdg.team22.sessionservice.infrastructure.lobby.db.entities.LobbyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LobbyJpaRepository extends JpaRepository<LobbyEntity, UUID> {
}

