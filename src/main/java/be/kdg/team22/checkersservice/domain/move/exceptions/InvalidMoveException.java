package be.kdg.team22.checkersservice.domain.move.exceptions;

public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(final String message) {
        super("Invalid move: " + message);
    }
}
