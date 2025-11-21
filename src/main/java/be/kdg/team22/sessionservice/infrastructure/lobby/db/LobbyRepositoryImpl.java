package be.kdg.team22.sessionservice.infrastructure.lobby.db;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.entities.LobbyEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LobbyRepositoryImpl implements LobbyRepository {
    private final LobbyJpaRepository repository;

    public LobbyRepositoryImpl(final LobbyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Lobby> findById(final LobbyId id) {
        return repository.findById(id.value()).map(LobbyEntity::toDomain);
    }

    @Override
    public List<Lobby> findAll() {
        return repository.findAll().stream().map(LobbyEntity::toDomain).toList();
    }

    @Override
    public void save(final Lobby lobby) {
        repository.save(LobbyEntity.fromDomain(lobby));
    }
}
