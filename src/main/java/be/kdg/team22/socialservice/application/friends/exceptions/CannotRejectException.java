package be.kdg.team22.socialservice.application.friends.exceptions;

public class CannotRejectException extends RuntimeException {
    public CannotRejectException() {
        super("Cannot reject friendship");
    }
}
