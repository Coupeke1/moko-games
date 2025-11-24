package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LobbyJpaRepository extends JpaRepository<LobbyEntity, UUID> {
}