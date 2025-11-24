package be.kdg.team22.sessionservice.domain.player.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class PlayerAlreadyInLobbyException extends RuntimeException {
    public PlayerAlreadyInLobbyException(PlayerId id) {
        super(String.format("Player '%s' is already in the lobby", id.value()));
    }

    public PlayerAlreadyInLobbyException(PlayerId playerId, LobbyId lobbyId) {
        super(String.format("Player '%s' is already in lobby '%s'", playerId.value(), lobbyId.value()));
    }
}