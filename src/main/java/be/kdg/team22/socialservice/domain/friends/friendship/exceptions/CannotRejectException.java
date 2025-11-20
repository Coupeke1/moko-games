package be.kdg.team22.socialservice.domain.friends.friendship.exceptions;

public class CannotRejectException extends RuntimeException {
    public CannotRejectException() {
        super("Cannot reject friendship");
    }
}
