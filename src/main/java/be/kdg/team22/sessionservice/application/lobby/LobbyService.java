package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.*;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.GameSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyService {
    private final LobbyRepository repository;
    private final PlayerService playerService;

    public LobbyService(final LobbyRepository repository, final PlayerService playerService) {
        this.repository = repository;
        this.playerService = playerService;
    }

    public Lobby createLobby(final GameId gameId, final PlayerId ownerId, final CreateLobbyModel model, final Jwt token) {
        Player owner = playerService.findPlayer(ownerId, token);
        LobbySettings settings = mapToDomainSettings(model.settings(), model.maxPlayers());
        Lobby lobby = new Lobby(gameId, owner, settings);

        repository.save(lobby);
        return lobby;
    }

    public Lobby findLobby(final LobbyId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public List<Lobby> findAllLobbies() {
        return repository.findAll();
    }

    public Lobby closeLobby(final LobbyId lobbyId, final PlayerId ownerId) {
        Lobby lobby = findLobby(lobbyId);
        lobby.close(ownerId);
        repository.save(lobby);
        return lobby;
    }

    public Lobby updateSettings(final LobbyId lobbyId, final PlayerId ownerId, final UpdateLobbySettingsModel model) {
        Lobby lobby = findLobby(lobbyId);
        LobbySettings newSettings = mapToDomainSettings(model.settings(), model.maxPlayers());
        lobby.changeSettings(ownerId, newSettings);
        repository.save(lobby);
        return lobby;
    }

    private LobbySettings mapToDomainSettings(final GameSettingsModel model, final Integer maxPlayers) {
        int resolvedMaxPlayers = maxPlayers != null ? maxPlayers : 4;

        GameSettings gameSettings = switch (model) {
            case TicTacToeSettingsModel t ->
                    new TicTacToeSettings(t.boardSize());
            case CheckersSettingsModel c ->
                    new CheckersSettings(c.boardSize(), c.flyingKings());
        };

        return new LobbySettings(gameSettings, resolvedMaxPlayers);
    }
}