package be.kdg.team22.storeservice.domain.cart.exceptions;

public class GameIdCannotBeNullException extends RuntimeException {
    public GameIdCannotBeNullException() {
        super("GameId cannot be null");
    }
}
