package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class LobbyFullException extends RuntimeException {
    public LobbyFullException(UUID lobbyId) {
        super(String.format("Lobby '%s' is full", lobbyId));
    }
}
