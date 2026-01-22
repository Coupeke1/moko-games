package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class CannotJoinClosedLobbyException extends RuntimeException {
    public CannotJoinClosedLobbyException() {
        super("Cannot join a lobby that is not open");
    }
}
