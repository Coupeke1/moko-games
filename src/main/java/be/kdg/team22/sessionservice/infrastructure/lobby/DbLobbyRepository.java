package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.LobbyEntity;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.LobbyJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbLobbyRepository implements LobbyRepository {
    private final LobbyJpaRepository repository;

    public DbLobbyRepository(final LobbyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Lobby> findById(final LobbyId id) {
        return repository.findById(id.value()).map(LobbyEntity::to);
    }

    @Override
    public Optional<Lobby> findByStartedGameId(final GameId gameInstanceId) {
        return repository.findByStartedGameId(gameInstanceId.value()).map(LobbyEntity::to);
    }

    @Override
    public List<Lobby> findAll() {
        return repository.findAll().stream().map(LobbyEntity::to).toList();
    }

    @Override
    public List<Lobby> findInvitesFromPlayerId(PlayerId id, GameId gameId) {
        return repository.findInvitesFromPlayerId(id.value(), gameId.value()).stream().map(LobbyEntity::to).toList();
    }

    @Override
    public void save(final Lobby lobby) {
        repository.save(LobbyEntity.from(lobby));
    }
}