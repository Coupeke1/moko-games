package be.kdg.team22.sessionservice.domain.lobby;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyRepository {
    Optional<Lobby> findById(LobbyId id);

    void save(Lobby lobby);
}
