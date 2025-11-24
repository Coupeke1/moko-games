package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class CannotRemoveOwnerException extends RuntimeException {
    public CannotRemoveOwnerException(LobbyId lobbyId) {
        super(String.format("Owner cannot be removed from lobby '%s'", lobbyId.value()));
    }
}
