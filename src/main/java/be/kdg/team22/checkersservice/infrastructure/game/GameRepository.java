package be.kdg.team22.checkersservice.infrastructure.game;

import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;

import java.util.Optional;

public interface GameRepository {
    void save(Game game);
    void delete(GameId id);
    Optional<Game> findById(GameId id);
}