package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class NotLobbyOwnerException extends RuntimeException {
    public NotLobbyOwnerException(UUID actingUserId) {
        super(String.format(
                "Only the lobby owner can manage the lobby (user %s)",
                actingUserId
        ));
    }
}