package be.kdg.team22.gamesservice.domain.game;

import org.jmolecules.ddd.annotation.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository {
    Optional<Game> findById(GameId id);

    List<Game> findAll();

    void save(Game game);
}
