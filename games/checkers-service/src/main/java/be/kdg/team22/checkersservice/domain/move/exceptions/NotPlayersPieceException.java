package be.kdg.team22.checkersservice.domain.move.exceptions;

public class NotPlayersPieceException extends RuntimeException {
    public NotPlayersPieceException(final int startingCell) {
        super("Piece at cell " + startingCell + " does not belong to current player");
    }
}
