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

    private final LobbyJpaRepository jpa;

    public LobbyRepositoryImpl(LobbyJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Lobby> findById(LobbyId id) {
        return jpa.findById(id.value()).map(LobbyEntity::toDomain);
    }

    @Override
    public List<Lobby> findAll() {
        return jpa.findAll()
                .stream()
                .map(LobbyEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Lobby lobby) {
        jpa.save(LobbyEntity.fromDomain(lobby));
    }
}
