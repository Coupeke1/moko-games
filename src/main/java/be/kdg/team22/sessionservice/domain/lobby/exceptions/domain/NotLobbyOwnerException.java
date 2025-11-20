package be.kdg.team22.sessionservice.domain.lobby.exceptions.domain;

import java.util.UUID;

public class NotLobbyOwnerException extends RuntimeException {
    public NotLobbyOwnerException(UUID actingUserId) {
        super("Only the lobby owner can manage the lobby (user " + actingUserId + ")");
    }
}
