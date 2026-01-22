package be.kdg.team22.checkersservice.domain.move.exceptions;

public class CapturedPieceNotOnLastTileException extends RuntimeException {
    public CapturedPieceNotOnLastTileException() {
        super("Captured piece must be on the last occupied tile in the path");
    }
}
