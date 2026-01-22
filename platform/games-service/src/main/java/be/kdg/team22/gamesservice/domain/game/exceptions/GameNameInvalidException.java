package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameNameInvalidException extends RuntimeException {
    public GameNameInvalidException() {
        super("Game name cannot be empty");
    }
}