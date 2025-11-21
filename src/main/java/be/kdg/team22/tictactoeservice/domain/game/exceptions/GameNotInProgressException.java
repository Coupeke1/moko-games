package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class GameNotInProgressException extends RuntimeException {
    public GameNotInProgressException() {
        super("Cannot make a move when game is not IN_PROGRESS");
    }
}
