package be.kdg.team22.gameaclservice.domain.exceptions.chess;

public class ChessGameNotReachableException extends RuntimeException {
    public ChessGameNotReachableException(String url) {
        super(String.format("chess game is not reachable at '%s'", url));
    }
}
