package be.kdg.team22.socialservice.domain.friends.user.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException() {
        super("Username is invalid");
    }
}
