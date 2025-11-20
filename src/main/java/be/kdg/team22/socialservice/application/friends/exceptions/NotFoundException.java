package be.kdg.team22.socialservice.application.friends.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Friendship with was not found");
    }
}
