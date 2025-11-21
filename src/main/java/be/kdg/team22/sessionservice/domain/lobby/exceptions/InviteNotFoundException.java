package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class InviteNotFoundException extends RuntimeException {
    public InviteNotFoundException(UUID lobbyId, UUID playerId) {
        super(String.format("No invite found in lobby '%s' for player '%s'", lobbyId, playerId));
    }
}
