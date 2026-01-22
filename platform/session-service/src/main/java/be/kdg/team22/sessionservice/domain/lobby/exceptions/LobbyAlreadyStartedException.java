package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class LobbyAlreadyStartedException extends RuntimeException {
    public LobbyAlreadyStartedException() {
        super("Lobby has already been started");
    }
}
