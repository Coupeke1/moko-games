package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class PlayerNotInLobbyException extends RuntimeException {
    public PlayerNotInLobbyException(UUID playerId, UUID lobbyId) {
        super(String.format("Player '%s' is not in lobby '%s'", playerId, lobbyId));
    }
}
