package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class AiMoveRequestFailedException extends RuntimeException {
    public AiMoveRequestFailedException(String message) {
        super(message);
    }
}
