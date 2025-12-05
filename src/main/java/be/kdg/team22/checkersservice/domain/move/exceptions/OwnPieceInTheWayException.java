package be.kdg.team22.checkersservice.domain.move.exceptions;

public class OwnPieceInTheWayException extends RuntimeException {
    public OwnPieceInTheWayException() {
        super("There is a piece of your own in the way");
    }
}
