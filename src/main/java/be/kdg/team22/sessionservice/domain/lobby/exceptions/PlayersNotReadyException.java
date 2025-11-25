package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class PlayersNotReadyException extends RuntimeException {
    public PlayersNotReadyException(UUID lobbyId) {
        super(String.format("Lobby '%s' cannot be started because not all players are ready", lobbyId));
    }
}
