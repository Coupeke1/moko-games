package be.kdg.team22.gamesservice.infrastructure.game;

import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.infrastructure.game.jpa.GameEntity;
import be.kdg.team22.gamesservice.infrastructure.game.jpa.GameJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbGameRepository implements GameRepository {

    private final GameJpaRepository jpa;

    public DbGameRepository(GameJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return jpa.findById(id.value()).map(GameEntity::toDomain);
    }

    @Override
    public List<Game> findAll() {
        return jpa.findAll().stream().map(GameEntity::toDomain).toList();
    }

    @Override
    public Optional<Game> findByName(String name) {
        return jpa.findByName(name).map(GameEntity::toDomain);
    }

    @Override
    public void save(Game game) {
        jpa.save(GameEntity.fromDomain(game));
    }
}