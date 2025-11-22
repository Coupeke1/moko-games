package be.kdg.team22.sessionservice.domain.player.exceptions;

public class NotReachableException extends RuntimeException {
    public NotReachableException() {
        super("Service 'user-service' is unreachable");
    }
}
