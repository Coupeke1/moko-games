package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
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

    public Lobby createLobby(final GameId gameId, final PlayerId ownerId) {
        if (gameId == null)
            throw new GameNotValidException(null);
        if (ownerId == null)
            throw new OwnerNotValidException(null);

        Lobby lobby = new Lobby(gameId, ownerId);
        repository.save(lobby);
        return lobby;
    }

    public Lobby findLobby(final LobbyId id) {
        return repository.findById(id).orElseThrow(() -> new LobbyNotFoundException(id.value()));
    }

    public List<Lobby> findAllLobbies() {
        return repository.findAll();
    }

    public Lobby closeLobby(LobbyId id, PlayerId actingUser) {
        Lobby lobby = findLobby(id);
        lobby.close(actingUser);
        lobbyRepository.save(lobby);
        return lobby;
    }

    public Lobby updateSettings(LobbyId id, PlayerId actingUser, LobbySettings newSettings) {
        Lobby lobby = findLobby(id);
        lobby.changeSettings(actingUser, newSettings);
        lobbyRepository.save(lobby);
        return lobby;
    }
}