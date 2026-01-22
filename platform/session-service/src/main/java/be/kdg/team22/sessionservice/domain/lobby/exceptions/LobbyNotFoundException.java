package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException(LobbyId id) {
        super(String.format("Lobby with id '%s' was not found", id.value()));
    }

    public LobbyNotFoundException(PlayerId id) {
        super(String.format("Lobby for player with id '%s' was not found", id.value()));
    }
}