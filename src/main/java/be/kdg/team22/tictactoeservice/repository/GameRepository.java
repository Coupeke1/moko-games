package be.kdg.team22.tictactoeservice.repository;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;

import java.util.Optional;

public interface GameRepository {
    void save(Game game);

    Optional<Game> findById(GameId id);
}