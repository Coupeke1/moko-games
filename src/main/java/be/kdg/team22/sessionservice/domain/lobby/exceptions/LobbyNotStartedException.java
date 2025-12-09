package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class LobbyNotStartedException extends RuntimeException {
    public LobbyNotStartedException(LobbyId id) {
        super(String.format("Lobby with id '%s' is not started and cannot be finished", id.value()));
    }
}

