package be.kdg.team22.checkersservice.domain.move.exceptions;

public class TargetCellNotEmptyException extends RuntimeException {
    public TargetCellNotEmptyException(final int targetCell) {
        super("Target cell " + targetCell + " is not empty");
    }
}
