package be.kdg.team22.sessionservice.domain.player.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class PlayerNotInLobbyException extends RuntimeException {

    public PlayerNotInLobbyException(PlayerId playerId, LobbyId lobbyId) {
        super(String.format("Player '%s' is not in lobby '%s'", playerId.value(), lobbyId.value()));
    }
}
