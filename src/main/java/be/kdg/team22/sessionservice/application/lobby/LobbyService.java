package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyCreationException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyService {

    private final LobbyRepository lobbyRepository;

    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public Lobby createLobby(GameId gameId, PlayerId ownerId) {

        if (gameId == null) throw new GameNotValidException(null);
        if (ownerId == null) throw new OwnerNotValidException(null);

        try {
            Lobby lobby = new Lobby(gameId, ownerId);
            lobbyRepository.save(lobby);
            return lobby;

        } catch (Exception e) {
            throw new LobbyCreationException("Could not create lobby: " + e.getMessage());
        }
    }

    public Lobby findLobby(LobbyId id) {
        return lobbyRepository.findById(id)
                .orElseThrow(() -> new LobbyNotFoundException(id.value()));
    }

    public List<Lobby> findAllLobbies() {
        return lobbyRepository.findAll();
    }
}
