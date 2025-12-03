package be.kdg.team22.checkersservice.application;

import be.kdg.team22.checkersservice.api.models.CreateGameModel;
import be.kdg.team22.checkersservice.domain.board.Move;
import be.kdg.team22.checkersservice.domain.game.Game;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.infrastructure.game.GameRepository;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final GameRepository repository;

    public GameService(final GameRepository repository) {
        this.repository = repository;
    }

    public Game create(final CreateGameModel model, final boolean aiPlayer) {
        List<PlayerId> players = model.players().stream().map(PlayerId::new).toList();
        Game game = Game.create(players, aiPlayer);
        repository.save(game);
        return game;
    }

    public Game getById(final GameId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Game requestMove(GameId gameId, Move move) {
        Game game = getById(gameId);
        game.requestMove(move);
        repository.save(game);
        return game;
    }
}