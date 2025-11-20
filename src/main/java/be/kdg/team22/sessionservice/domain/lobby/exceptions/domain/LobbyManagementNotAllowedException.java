package be.kdg.team22.sessionservice.domain.lobby.exceptions.domain;

import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;

public class LobbyManagementNotAllowedException extends RuntimeException {
    public LobbyManagementNotAllowedException(LobbyStatus status) {
        super("Lobby cannot be managed in state " + status);
    }
}
