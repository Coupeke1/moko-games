package be.kdg.team22.checkersservice.domain.move.exceptions;

public class NotEnoughTilesException extends RuntimeException {
    public NotEnoughTilesException() {
        super("You can not move to the same tile");
    }
}
