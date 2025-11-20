package be.kdg.team22.socialservice.domain.friends.friendship.exceptions;

public class CannotAcceptException extends RuntimeException {
    public CannotAcceptException() {
        super("Cannot accept friendship");
    }
}
