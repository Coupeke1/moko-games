package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class PlayerAlreadyInLobbyException extends RuntimeException {
    public PlayerAlreadyInLobbyException(UUID playerId) {
        super(String.format("Player '%s' is already in the lobby", playerId));
    }
}
