package be.kdg.team22.checkersservice.domain.game.exceptions;

public class UniquePlayersException extends RuntimeException {
    public UniquePlayersException() {
        super("All players should be unique");
    }
}