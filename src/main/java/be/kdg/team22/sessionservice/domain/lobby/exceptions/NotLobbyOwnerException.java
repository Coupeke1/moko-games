package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class NotLobbyOwnerException extends RuntimeException {
    public NotLobbyOwnerException(PlayerId actingUserId) {
        super(String.format(
                "Only the lobby owner can manage the lobby (user %s)",
                actingUserId.value()
        ));
    }
}