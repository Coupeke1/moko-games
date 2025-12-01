package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class NoAiPlayerException extends RuntimeException {
    public NoAiPlayerException(LobbyId id) {
        super("Lobby " + id.value() + " has no AI player.");
    }
}
