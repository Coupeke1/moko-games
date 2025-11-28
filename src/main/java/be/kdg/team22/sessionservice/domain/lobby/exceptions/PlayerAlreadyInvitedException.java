package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class PlayerAlreadyInvitedException extends RuntimeException {
    public PlayerAlreadyInvitedException(PlayerId id) {
        super(String.format("Player '%s' is already invited for the lobby", id.value()));
    }

    public PlayerAlreadyInvitedException(PlayerId playerId, LobbyId lobbyId) {
        super(String.format("Player '%s' is already invited for lobby '%s'", playerId.value(), lobbyId.value()));
    }
}