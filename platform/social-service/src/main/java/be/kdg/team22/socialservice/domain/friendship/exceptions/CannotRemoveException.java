package be.kdg.team22.socialservice.domain.friendship.exceptions;

public class CannotRemoveException extends RuntimeException {
    public CannotRemoveException() {
        super("Cannot remove friendship");
    }
}
