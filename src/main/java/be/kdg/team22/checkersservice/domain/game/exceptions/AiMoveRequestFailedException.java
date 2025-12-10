package be.kdg.team22.checkersservice.domain.game.exceptions;

public class AiMoveRequestFailedException extends RuntimeException {
    public AiMoveRequestFailedException(String message) {
        super(message);
    }
}
