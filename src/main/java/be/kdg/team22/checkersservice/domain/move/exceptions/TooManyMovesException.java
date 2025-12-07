package be.kdg.team22.checkersservice.domain.move.exceptions;

public class TooManyMovesException extends RuntimeException {
    public TooManyMovesException() {
        super("You can only make 1 move, or multiple captures in 1 turn");
    }
}
