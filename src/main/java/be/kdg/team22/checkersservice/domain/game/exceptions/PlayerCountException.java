package be.kdg.team22.checkersservice.domain.game.exceptions;

public class PlayerCountException extends RuntimeException {
    public PlayerCountException() {
        super("There should be exactly 2 players");
    }
}
