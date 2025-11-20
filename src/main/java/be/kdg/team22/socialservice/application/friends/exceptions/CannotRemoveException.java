package be.kdg.team22.socialservice.application.friends.exceptions;

public class CannotRemoveException extends RuntimeException {
    public CannotRemoveException() {
        super("Cannot remove friendship");
    }
}
