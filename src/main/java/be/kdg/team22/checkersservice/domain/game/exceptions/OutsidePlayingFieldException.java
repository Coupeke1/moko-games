package be.kdg.team22.checkersservice.domain.game.exceptions;

public class OutsidePlayingFieldException extends RuntimeException {
    public OutsidePlayingFieldException() {
        super("This cell is not within the playing field");
    }
}
