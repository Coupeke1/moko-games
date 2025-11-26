package be.kdg.team22.tictactoeservice.infrastructure.game;

import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final Map<GameId, Game> games = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        games.put(game.id(), game);
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return Optional.ofNullable(games.get(id));
    }
}