package be.kdg.team22.socialservice.domain.friendship.exceptions;

public class NoMatchingUsersException extends RuntimeException {
    public NoMatchingUsersException() {
        super("Users do not match");
    }
}
