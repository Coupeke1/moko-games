package be.kdg.team22.gamesservice.domain.game;

import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository {
    Optional<Game> findById(GameId id);

    List<Game> findAll();

    Optional<Game> findByName(String name);

    Optional<Achievement> findAchievementById(AchievementKey key, GameId id);

    void save(Game game);
}
