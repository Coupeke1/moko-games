package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class LobbyNotFoundByGameInstanceException extends RuntimeException {
    public LobbyNotFoundByGameInstanceException(UUID gameInstanceId) {
        super(String.format("Lobby with game instance id '%s' was not found", gameInstanceId));
    }
}

