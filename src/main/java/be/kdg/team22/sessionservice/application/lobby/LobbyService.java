package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CheckersSettingsModel;
import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.api.lobby.models.TicTacToeSettingsModel;
import be.kdg.team22.sessionservice.api.lobby.models.UpdateLobbySettingsModel;
import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbySettingsInvalidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.GameSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyService {
    private final LobbyRepository repository;

    public LobbyService(final LobbyRepository repository) {
        this.repository = repository;
    }

    public Lobby createLobby(final GameId gameId, final PlayerId ownerId, final CreateLobbyModel model) {
        if (gameId == null) throw new GameNotValidException(null);
        if (ownerId == null) throw new OwnerNotValidException(null);

        int maxPlayers = model.maxPlayers() != null ? model.maxPlayers() : 4;
        int boardSize = model.boardSize() != null ? model.boardSize() : 3;

        // voorlopig 1 game: Tic-Tac-Toe
        LobbySettings settings = new LobbySettings(
                new TicTacToeSettings(boardSize),
                maxPlayers
        );

        Lobby lobby = new Lobby(gameId, ownerId, settings);
        repository.save(lobby);
        return lobby;
    }

    public Lobby findLobby(final LobbyId id) {
        return repository.findById(id).orElseThrow(() -> new LobbyNotFoundException(id.value()));
    }

    public List<Lobby> findAllLobbies() {
        return repository.findAll();
    }

    public Lobby closeLobby(final LobbyId id, final PlayerId actingUser) {
        Lobby lobby = findLobby(id);
        lobby.close(actingUser);
        repository.save(lobby);
        return lobby;
    }

    public Lobby updateSettings(
            final LobbyId id,
            final PlayerId actingUser,
            final UpdateLobbySettingsModel model
    ) {
        Lobby lobby = findLobby(id);

        LobbySettings newSettings = mapToDomainSettings(model);

        lobby.changeSettings(actingUser, newSettings);
        repository.save(lobby);
        return lobby;
    }

    private LobbySettings mapToDomainSettings(final UpdateLobbySettingsModel model) {
        if (model == null || model.settings() == null) {
            throw new LobbySettingsInvalidException("Settings cannot be null");
        }

        int maxPlayers = model.maxPlayers();

        GameSettings gameSettings = switch (model.settings()) {
            case TicTacToeSettingsModel t -> new TicTacToeSettings(t.boardSize());
            case CheckersSettingsModel c -> new CheckersSettings(c.boardSize(), c.flyingKings());
        };

        return new LobbySettings(gameSettings, maxPlayers);
    }
}
