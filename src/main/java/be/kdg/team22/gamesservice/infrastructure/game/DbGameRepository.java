package be.kdg.team22.gamesservice.infrastructure.game;

import be.kdg.team22.gamesservice.domain.game.*;
import be.kdg.team22.gamesservice.infrastructure.game.jpa.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbGameRepository implements GameRepository {

    private final GameJpaRepository jpa;
    private final AchievementJpaRepository achievementJpa;

    public DbGameRepository(GameJpaRepository jpa,  AchievementJpaRepository achievementJpa) {
        this.jpa = jpa;
        this.achievementJpa = achievementJpa;
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
    public Optional<Achievement> findAchievementById(AchievementKey key, GameId id) {
        return achievementJpa.findById(new AchievementId(key.key(), id.value())).map(AchievementEntity::toDomain);
    }

    @Override
    public void save(Game game) {
        jpa.save(GameEntity.fromDomain(game));
    }
}