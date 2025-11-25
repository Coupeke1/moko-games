package be.kdg.team22.gamesservice.infrastructure.db.repositories;

import be.kdg.team22.gamesservice.domain.Game;
import be.kdg.team22.gamesservice.domain.GameId;
import be.kdg.team22.gamesservice.domain.GameRepository;
import be.kdg.team22.gamesservice.infrastructure.db.entities.GameEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GameRepositoryImpl implements GameRepository {

    private final JpaGameRepository jpa;

    public GameRepositoryImpl(JpaGameRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return jpa.findById(id.value())
                .map(GameEntity::toDomain);
    }
}