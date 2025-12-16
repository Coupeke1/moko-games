package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class BotMoveRequestFailedException extends RuntimeException {
    public BotMoveRequestFailedException(String message) {
        super(message);
    }
}
