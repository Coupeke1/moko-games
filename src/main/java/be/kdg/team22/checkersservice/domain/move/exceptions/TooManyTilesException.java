package be.kdg.team22.checkersservice.domain.move.exceptions;

public class TooManyTilesException extends RuntimeException {
    public TooManyTilesException(final int moveCount) {
        super("This piece can only move " + moveCount + " tiles, or " + (moveCount + 1) + " when capturing an opponent's piece");
    }
    public TooManyTilesException(final int moveCount, final boolean capture) {
        super("This piece can only move " + moveCount + " tiles " + (capture ? "when capturing an opponent's piece" : ""));
    }
}
