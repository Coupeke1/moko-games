package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotValidException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyCreationException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.OwnerNotValidException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;

    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    @Transactional
    public Lobby createLobby(UUID rawGameId, UUID rawOwnerId) {

        if (rawGameId == null) throw new GameNotValidException(null);
        if (rawOwnerId == null) throw new OwnerNotValidException(null);

        GameId gameId = GameId.from(rawGameId);
        PlayerId ownerId = PlayerId.from(rawOwnerId);

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
}
