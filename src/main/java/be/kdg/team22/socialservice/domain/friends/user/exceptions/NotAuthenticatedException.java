package be.kdg.team22.socialservice.domain.friends.user.exceptions;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException() {
        super("User is not authenticated");
    }
}
