package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class LobbyFullException extends RuntimeException {
    public LobbyFullException(LobbyId id) {
        super(String.format("Lobby '%s' is full", id.value()));
    }
}
