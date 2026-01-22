package be.kdg.team22.checkersservice.domain.move.exceptions;

public class StartingPieceNotFoundException extends RuntimeException {
    public StartingPieceNotFoundException(final int startingCell) {
        super("No piece at starting cell " + startingCell);
    }
}
