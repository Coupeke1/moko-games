package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.BoardSizeProperties;
import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameId;
import be.kdg.team22.tictactoeservice.domain.NotFoundException;
import be.kdg.team22.tictactoeservice.domain.PlayerId;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final BoardSizeProperties boardConfig;

    public GameService(BoardSizeProperties boardConfig, GameRepository gameRepository) {
        this.boardConfig = boardConfig;
        this.gameRepository = gameRepository;
    }

    public Game startGame(int requestedSize, PlayerId playerXId, PlayerId PlayerOId) {
        if (requestedSize < boardConfig.getMinSize() || requestedSize > boardConfig.getMaxSize()) {
            throw new IllegalArgumentException("Board size must be between " + boardConfig.getMinSize() + " and " + boardConfig.getMaxSize());
        }

        Game game = new Game(requestedSize, playerXId, PlayerOId);
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
}
