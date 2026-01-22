package be.kdg.team22.checkersservice.domain.move.exceptions;

public class TooManyPiecesException extends RuntimeException {
    public TooManyPiecesException() {
        super("You cannot capture multiple pieces in one row");
    }
}
