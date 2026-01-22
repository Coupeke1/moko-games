package be.kdg.team22.checkersservice.domain.register.exceptions;

public class GameNotRegisteredException extends RuntimeException {
    public GameNotRegisteredException() {
        super("Game not registered");
    }
}
