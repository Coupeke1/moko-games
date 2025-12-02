package be.kdg.team22.checkersservice.domain.game.exceptions;

public class GameIdException extends RuntimeException {
    public GameIdException() {
        super("Game value is not valid");
    }
}