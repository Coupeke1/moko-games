package be.kdg.team22.userservice.domain.library.exceptions;

public class GameServiceNotReachableException extends RuntimeException {
    public GameServiceNotReachableException(String url) {
        super(String.format("Game-service is not reachable at '%s'", url));
    }
}
