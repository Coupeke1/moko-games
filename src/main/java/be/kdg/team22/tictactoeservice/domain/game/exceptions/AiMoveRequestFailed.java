package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class AiMoveRequestFailed extends RuntimeException {
    public AiMoveRequestFailed(String message) {
        super(message);
    }
}
