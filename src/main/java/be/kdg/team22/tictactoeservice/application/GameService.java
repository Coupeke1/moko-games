package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private final GameRepository repository;
    private final BoardSizeProperties config;

    public GameService(final GameRepository repository, final BoardSizeProperties config) {
        this.repository = repository;
        this.config = config;
    }

    public Game startGame(final int requestedSize, final List<Player> players) {
        Game game = Game.create(config.minSize(), config.maxSize(), requestedSize, players);
        repository.save(game);
        return game;
    }

    public Game getGame(final GameId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Game resetGame(final GameId id) {
        Game game = getGame(id);
        game.reset();
        repository.save(game);
        return game;
    }

    public Game requestMove(final GameId id, final Move move) {
        Game game = getGame(id);
        game.requestMove(move);
        nextPlayer(id);
        return game;
    }

    public Player nextPlayer(final GameId id) {
        Game game = getGame(id);
        Player nextPlayer = game.nextPlayer();
        repository.save(game);
        return nextPlayer;
    }
}