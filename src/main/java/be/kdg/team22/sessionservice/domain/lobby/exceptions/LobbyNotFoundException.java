package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException(LobbyId id) {
        super(String.format("Lobby with id '%s' was not found", id.value()));
    }
}