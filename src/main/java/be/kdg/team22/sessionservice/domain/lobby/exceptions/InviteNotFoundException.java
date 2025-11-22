package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class InviteNotFoundException extends RuntimeException {
    public InviteNotFoundException(LobbyId lobbyId, PlayerId playerId) {
        super(String.format("No invite found in lobby '%s' for player '%s'", lobbyId.value(), playerId.value()));
    }
}
