package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException(UUID id) {
        super(String.format("Lobby with id '%s' was not found", id));
    }
}