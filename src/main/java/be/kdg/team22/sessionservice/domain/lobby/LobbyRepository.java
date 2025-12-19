package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LobbyRepository {
    Optional<Lobby> findById(LobbyId id);

    Optional<Lobby> findByStartedGameId(GameId gameInstanceId);
    Optional<Lobby> findByPlayerId(PlayerId id);

    List<Lobby> findAll();

    void save(Lobby lobby);

    List<Lobby> findInvitesFromPlayerId(PlayerId id, GameId game);
}
