package be.kdg.team22.userservice.infrastructure.image.exceptions;

public class NotReachableException extends RuntimeException {
    public NotReachableException() {
        super("Service 'cat-api' is unreachable");
    }
}
