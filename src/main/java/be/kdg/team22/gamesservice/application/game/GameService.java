package be.kdg.team22.gamesservice.application.game;

import be.kdg.team22.gamesservice.api.game.models.GameSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.GameRepository;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.PlayersListEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    public GameService(final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void startGame(final StartGameRequest command) {
        if (command.players() == null || command.players().isEmpty()) {
            throw new PlayersListEmptyException();
        }

        GameSettingsModel settings = command.settings();
        if (settings == null) {
            throw new IllegalArgumentException("Game settings cannot be null");
        }

        GameId gameId = new GameId(command.gameId());
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        log.info(
                "Starting game '{}' for lobby {} with {} players and settings type {}",
                game.name(),
                command.lobbyId(),
                command.players().size(),
                settings.getClass().getSimpleName()
        );

        // TODO: in toekomstige sprint HTTP-call doen naar externe game engine
    }
}
