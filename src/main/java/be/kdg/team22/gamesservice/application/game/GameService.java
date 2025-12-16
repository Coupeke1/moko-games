package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.domain.game.*;
import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsValidator;
import be.kdg.team22.gamesservice.infrastructure.game.engine.ExternalGamesRepository;
import be.kdg.team22.gamesservice.infrastructure.game.health.GameHealthChecker;
import be.kdg.team22.gamesservice.infrastructure.store.ExternalStoreRepository;
import be.kdg.team22.gamesservice.infrastructure.store.NewStoreEntryModel;
import be.kdg.team22.gamesservice.infrastructure.store.StoreEntryModel;
import be.kdg.team22.gamesservice.infrastructure.store.UpdateStoreEntryModel;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public StartGameResponseModel startGame(final StartGameRequest request, final Jwt token) {

        if (request.players() == null || request.players().isEmpty()) {
            throw new PlayersListEmptyException();
        }

        if (request.settings() == null) {
            throw InvalidGameSettingsException.missingSettings();
        }

        GameId id = GameId.from(request.gameId());
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        Map<String, Object> resolvedSettings = game.validateSettings(request.settings());

        StartGameRequest resolvedRequest = new StartGameRequest(
                request.lobbyId(),
                request.gameId(),
                request.players(),
                resolvedSettings,
                request.aiPlayer()
        );

        UUID instanceId = engine.startExternalGame(game, resolvedRequest, token);

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

    public Game create(final RegisterGameRequest request) {
        if (gameRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateGameNameException(request.name());
        }

        GameSettingsValidator.validateDefinition(request.settingsDefinition());

        if (!gameHealthChecker.isHealthy(request)) throw new GameUnhealthyException(request.name());

        Game game = Game.register(request);
        game.updateHealthStatus(true);
        gameRepository.save(game);

        handleStoreEntry(game.id(), request.price(), request.category());

        return game;
    }

    public Game register(final String name, final RegisterGameRequest request) {
        Optional<Game> optionalGame = gameRepository.findByName(name);
        if (optionalGame.isEmpty()) return create(request);

        Game game = optionalGame.get();
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

    public List<Achievement> getAchievements(final GameId id) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException(id));
        return game.achievements().stream().toList();
    }

    public Achievement getAchievement(final GameId id, final AchievementKey key) {
        return gameRepository.findAchievementById(key, id)
                .orElseThrow(() -> new AchievementNotFoundException(key, id));
    }
}