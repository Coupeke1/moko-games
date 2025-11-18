package be.kdg.team22.tictactoeservice.repository;

import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final Map<GameId, Game> games = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        games.put(game.getId(), game);
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return Optional.ofNullable(games.get(id));
    }
}
