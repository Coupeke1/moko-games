package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameUnhealthyException extends RuntimeException {
    public GameUnhealthyException(String name) {
        super(String.format("Game '%s' failed health check during registration", name));
    }
}
