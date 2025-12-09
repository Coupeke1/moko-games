package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import be.kdg.team22.sessionservice.domain.lobby.LobbyId;

public class LobbyClosedException extends RuntimeException {
    public LobbyClosedException(LobbyId id, String status) {
        super(String.format("Lobby with id '%s' is in status '%s' and cannot be accessed", id.value(), status));
    }
}