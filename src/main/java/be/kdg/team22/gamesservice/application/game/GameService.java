package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidGameConfigurationException;
import be.kdg.team22.gamesservice.domain.game.exceptions.PlayersListEmptyException;
import be.kdg.team22.gamesservice.infrastructure.game.engine.ExternalGamesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ExternalGamesRepository engine;

    public GameService(GameRepository gameRepository, ExternalGamesRepository engine) {
        this.gameRepository = gameRepository;
        this.engine = engine;
    }

    public StartGameResponseModel startGame(StartGameRequest request) {

        if (request.players() == null || request.players().isEmpty()) {
            throw new PlayersListEmptyException();
        }

        if (request.settings() == null) {
            throw new InvalidGameConfigurationException("Game settings cannot be null");
        }

        GameId id = GameId.from(request.gameId());
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        UUID instanceId = engine.startExternalGame(game, request);

        return new StartGameResponseModel(instanceId);
    }

    public Game findById(GameId id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }
}