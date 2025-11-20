package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
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

        // voorlopig 1 game: TicTacToe
        LobbySettings settings = new LobbySettings(new TicTacToeSettings(boardSize), maxPlayers);

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

    public Lobby updateSettings(final LobbyId id, final PlayerId actingUser, final int maxPlayers) {
        Lobby lobby = findLobby(id);
        LobbySettings newSettings = new LobbySettings(lobby.settings().gameSettings(), maxPlayers);
        lobby.changeSettings(actingUser, newSettings);
        repository.save(lobby);
        return lobby;
    }
}
