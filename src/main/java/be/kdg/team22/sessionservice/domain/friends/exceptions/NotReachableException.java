package be.kdg.team22.sessionservice.domain.friends.exceptions;

public class NotReachableException extends RuntimeException {
    public NotReachableException() {
        super("Service 'social-service' is unreachable");
    }
}
