package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class CannotRemoveOwnerException extends RuntimeException {
    public CannotRemoveOwnerException(UUID lobbyId) {
        super(String.format("Owner cannot be removed from lobby '%s'", lobbyId));
    }
}
