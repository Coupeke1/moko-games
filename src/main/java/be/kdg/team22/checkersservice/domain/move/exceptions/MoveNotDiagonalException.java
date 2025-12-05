package be.kdg.team22.checkersservice.domain.move.exceptions;

public class MoveNotDiagonalException extends RuntimeException {
    public MoveNotDiagonalException() {
        super("Move must be diagonal");
    }
}
