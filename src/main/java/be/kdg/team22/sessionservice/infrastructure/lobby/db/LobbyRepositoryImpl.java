package be.kdg.team22.sessionservice.infrastructure.lobby.db;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.entities.LobbyEntity;
import org.springframework.stereotype.Repository;

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
    public void save(Lobby lobby) {
        jpa.save(LobbyEntity.fromDomain(lobby));
    }
}
