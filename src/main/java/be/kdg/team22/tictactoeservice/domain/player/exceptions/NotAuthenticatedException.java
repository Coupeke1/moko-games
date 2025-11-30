package be.kdg.team22.tictactoeservice.domain.player.exceptions;

public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException() {
        super("User is not authenticated");
    }
}
