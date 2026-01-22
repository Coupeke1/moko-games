package be.kdg.team22.checkersservice.infrastructure.game.memory;

import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.infrastructure.game.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Deprecated
public class InMemoryGameRepository implements GameRepository {
    private final Map<GameId, Game> games = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        games.put(game.id(), game);
    }

    @Override
    public void delete(GameId id) {
        games.remove(id);
    }

    @Override
    public Optional<Game> findById(GameId id) {
        return Optional.ofNullable(games.get(id));
    }
}