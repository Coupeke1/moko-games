package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class LobbyStateInvalidException extends RuntimeException {
    public LobbyStateInvalidException(UUID lobbyId, String status) {
        super(String.format("Lobby '%s' cannot be modified in state '%s'", lobbyId, status));
    }
}