package be.kdg.team22.socialservice.domain.friendship.exceptions;

public class CannotRejectException extends RuntimeException {
    public CannotRejectException() {
        super("Cannot reject friendship");
    }
}
