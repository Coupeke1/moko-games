package be.kdg.team22.checkersservice.domain.game;

import java.util.Optional;

public interface GameRepository {
    void save(Game game);
    void delete(GameId id);
    Optional<Game> findById(GameId id);
}