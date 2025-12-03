package be.kdg.team22.checkersservice.domain.game.exceptions;

public class GameNotRunningException extends RuntimeException {
    public GameNotRunningException() {
        super("Cannot make a move when game is not running");
    }
}