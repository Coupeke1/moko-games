package be.kdg.team22.socialservice.application.friends.exceptions;

public class CannotAcceptException extends RuntimeException {
    public CannotAcceptException() {
        super("Cannot accept friendship");
    }
}
