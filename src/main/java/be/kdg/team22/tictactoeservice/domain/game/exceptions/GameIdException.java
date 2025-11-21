package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class GameIdException extends RuntimeException {
    public GameIdException() {
        super("Game value is not valid");
    }
}