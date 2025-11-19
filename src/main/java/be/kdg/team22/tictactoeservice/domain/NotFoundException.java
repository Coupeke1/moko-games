package be.kdg.team22.tictactoeservice.domain;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }
}
