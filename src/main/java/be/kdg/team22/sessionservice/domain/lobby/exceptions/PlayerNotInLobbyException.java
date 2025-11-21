package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.PlayerId;

import java.util.UUID;

public class PlayerNotInLobbyException extends RuntimeException {
    public PlayerNotInLobbyException(UUID playerId) {
        super(String.format("Player '%s' is not in the lobby", playerId));
    }

    public PlayerNotInLobbyException(PlayerId playerId, UUID lobbyId) {
        super(String.format("Player '%s' is not in lobby '%s'", playerId.value(), lobbyId));
    }
}
