package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class LobbyStateInvalidException extends RuntimeException {
    public LobbyStateInvalidException(LobbyId lobbyId, String status) {
        super(String.format("Lobby '%s' cannot be modified in state '%s'", lobbyId.value(), status));
    }
}