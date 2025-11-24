package be.kdg.team22.sessionservice.domain.lobby.exceptions;

public class NotReachableException extends RuntimeException {
    public NotReachableException() {
        super("Service 'game-service' is unreachable");
    }
}
