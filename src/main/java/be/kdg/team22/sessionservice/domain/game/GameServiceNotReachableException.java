package be.kdg.team22.sessionservice.domain.game;

public class GameServiceNotReachableException extends RuntimeException {
    public GameServiceNotReachableException() {
        super("Game service is not reachable");
    }
}

