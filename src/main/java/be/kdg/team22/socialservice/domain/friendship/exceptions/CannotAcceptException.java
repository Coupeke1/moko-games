package be.kdg.team22.socialservice.domain.friendship.exceptions;

public class CannotAcceptException extends RuntimeException {
    public CannotAcceptException() {
        super("Cannot accept friendship");
    }
}
