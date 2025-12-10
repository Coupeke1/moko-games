package be.kdg.team22.gamesservice.domain.game.exceptions;

public class StoreServiceNotReachableException extends RuntimeException {
    public StoreServiceNotReachableException(String url) {
        super(String.format("store-service is not reachable at '%s'", url));
    }
}
