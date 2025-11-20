package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class PlayerNotInLobbyException extends RuntimeException {
    public PlayerNotInLobbyException(UUID playerId) {
        super(String.format("Player '%s' is not in the lobby", playerId));
    }
}
