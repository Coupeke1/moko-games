package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.*;
import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.settings.CheckersSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.GameSettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.ExternalUserRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyService {
    private final LobbyRepository repository;
    private final ExternalUserRepository externalUserRepository;

    public LobbyService(final LobbyRepository repository, ExternalUserRepository externalUserRepository) {
        this.repository = repository;
        this.externalUserRepository = externalUserRepository;
    }

    public Lobby createLobby(GameId gameId, PlayerId ownerId, CreateLobbyModel model, String token) {
        UserResponse owner = externalUserRepository.getById(ownerId.value(), token);
        LobbyPlayer ownerPlayer = new LobbyPlayer(owner.id(), owner.username());
        LobbySettings settings = mapToDomainSettings(model.settings(), model.maxPlayers());
        Lobby lobby = new Lobby(gameId, ownerPlayer, settings);
        repository.save(lobby);
        return lobby;
    }

    public Lobby findLobby(final LobbyId id) {
        return repository.findById(id)
                .orElseThrow(() -> new LobbyNotFoundException(id.value()));
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
        LobbySettings newSettings = mapToDomainSettings(model.settings(), model.maxPlayers());
        lobby.changeSettings(actingUser, newSettings);
        repository.save(lobby);
        return lobby;
    }

    private LobbySettings mapToDomainSettings(GameSettingsModel model, Integer maxPlayers) {
        int resolvedMaxPlayers = maxPlayers != null ? maxPlayers : 4;

        GameSettings gameSettings = switch (model) {
            case TicTacToeSettingsModel t -> new TicTacToeSettings(t.boardSize());
            case CheckersSettingsModel c -> new CheckersSettings(c.boardSize(), c.flyingKings());
        };

        return new LobbySettings(gameSettings, resolvedMaxPlayers);
    }
}
