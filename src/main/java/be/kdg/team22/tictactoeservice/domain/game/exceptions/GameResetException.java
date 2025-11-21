package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class GameResetException extends RuntimeException {
    public GameResetException() {
        super("Cannot reset game when it is IN_PROGRESS");
    }
}