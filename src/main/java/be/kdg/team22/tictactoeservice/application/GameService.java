package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.NotFoundException;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.player.Player;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final BoardSizeProperties boardConfig;

    public GameService(BoardSizeProperties boardConfig, GameRepository gameRepository) {
        this.boardConfig = boardConfig;
        this.gameRepository = gameRepository;
    }

    public Game startGame(int requestedSize, List<Player> players) {
        Game game = Game.create(boardConfig.getMinSize(), boardConfig.getMaxSize(), requestedSize, players);
        gameRepository.save(game);
        return game;
    }

    public Game getGame(GameId id) {
        return gameRepository.findById(id).orElseThrow(() -> new NotFoundException("Game with id " + id.id() + " not found"));
    }

    public Game resetGame(GameId id) {
        Game game = getGame(id);
        game.reset();
        gameRepository.save(game);
        return game;
    }

    public Player nextPlayer(GameId id) {
        Game game = getGame(id);
        Player nextPlayer = game.nextPlayer();
        gameRepository.save(game);
        return nextPlayer;
    }
}
