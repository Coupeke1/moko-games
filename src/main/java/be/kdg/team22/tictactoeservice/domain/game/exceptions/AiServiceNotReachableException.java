package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class AiServiceNotReachableException extends RuntimeException {
    public AiServiceNotReachableException(String url) {
        super(String.format("Ai-service is not reachable at '%s'", url));
    }
}
