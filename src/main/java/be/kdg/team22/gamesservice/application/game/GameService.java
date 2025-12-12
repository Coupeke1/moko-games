package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameCategory;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import be.kdg.team22.gamesservice.infrastructure.game.engine.ExternalGamesRepository;
import be.kdg.team22.gamesservice.infrastructure.game.health.GameHealthChecker;
import be.kdg.team22.gamesservice.infrastructure.store.ExternalStoreRepository;
import be.kdg.team22.gamesservice.infrastructure.store.NewStoreEntryModel;
import be.kdg.team22.gamesservice.infrastructure.store.StoreEntryModel;
import be.kdg.team22.gamesservice.infrastructure.store.UpdateStoreEntryModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final GameHealthChecker gameHealthChecker;
    private final ExternalStoreRepository externalStoreRepository;
    private final ExternalGamesRepository engine;

    public GameService(final GameRepository gameRepository, final GameHealthChecker gameHealthChecker, final ExternalStoreRepository externalStoreRepository, final ExternalGamesRepository engine) {
        this.gameRepository = gameRepository;
        this.gameHealthChecker = gameHealthChecker;
        this.externalStoreRepository = externalStoreRepository;
        this.engine = engine;
    }

    public StartGameResponseModel startGame(final StartGameRequest request) {

        if (request.players() == null || request.players().isEmpty()) {
            throw new PlayersListEmptyException();
        }

        if (request.settings() == null) {
            throw new InvalidGameConfigurationException("Game settings cannot be null");
        }

        GameId id = GameId.from(request.gameId());
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        game.validateSettings(request.settings());

        UUID instanceId = engine.startExternalGame(game, request);

        return new StartGameResponseModel(instanceId);
    }

    public Game findById(final GameId id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public Game findByName(final String name) {
        return gameRepository.findByName(name)
                .orElseThrow(() -> new GameNotFoundException(name));
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public Game register(final RegisterGameRequest request) {
        if (gameRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateGameNameException(request.name());
        }

        boolean isHealthy = gameHealthChecker.isHealthy(request);
        if (!isHealthy) {
            throw new GameUnhealthyException(request.name());
        }

        Game game = Game.register(request);
        game.updateHealthStatus(true);
        gameRepository.save(game);

        handleStoreEntry(game.id(), request.price(), request.category());

        return game;
    }

    public Game update(final GameId id, final RegisterGameRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        if (!game.name().equals(request.name()) && gameRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateGameNameException(request.name());
        }

        boolean isHealthy = gameHealthChecker.isHealthy(request);
        if (!isHealthy) {
            throw new GameUnhealthyException(request.name());
        }

        game.update(request);
        game.updateHealthStatus(true);
        gameRepository.save(game);

        handleStoreEntry(game.id(), request.price(), request.category());

        return game;
    }

    private void handleStoreEntry(GameId id, BigDecimal price, GameCategory category) {
        StoreEntryModel storeEntry = externalStoreRepository.getStoreEntry(id);
        if (storeEntry != null) externalStoreRepository.updateStoreEntry(id, new UpdateStoreEntryModel(
                price,
                category
        ));
        else externalStoreRepository.createStoreEntry(new NewStoreEntryModel(
                id.value(),
                price,
                category
        ));
    }
}