package be.kdg.team22.sessionservice.domain.lobby;

import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LobbyRepository {
    Optional<Lobby> findById(LobbyId id);

    List<Lobby> findAll();

    void save(Lobby lobby);
}
