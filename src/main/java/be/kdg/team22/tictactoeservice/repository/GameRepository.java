package be.kdg.team22.tictactoeservice.repository;

import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameId;

import java.util.Optional;

public interface GameRepository {
    void save(Game game);
    Optional<Game> findById(GameId id);
}
