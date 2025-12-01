package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class NoBotException extends RuntimeException {
    public NoBotException(LobbyId id) {
        super("Lobby " + id.value() + " has no AI player.");
    }
}
