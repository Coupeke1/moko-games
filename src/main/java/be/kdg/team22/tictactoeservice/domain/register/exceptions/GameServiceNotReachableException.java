package be.kdg.team22.tictactoeservice.domain.register.exceptions;

public class GameServiceNotReachableException extends RuntimeException {
    public GameServiceNotReachableException(String url) {
        super(String.format("game-service is not reachable at '%s'", url));
    }
}
