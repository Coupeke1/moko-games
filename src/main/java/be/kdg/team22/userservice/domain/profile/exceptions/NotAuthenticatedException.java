package be.kdg.team22.userservice.domain.profile.exceptions;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException() {
        super("User is not authenticated");
    }
}
