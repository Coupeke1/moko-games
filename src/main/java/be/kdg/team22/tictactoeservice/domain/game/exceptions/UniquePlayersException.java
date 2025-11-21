package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class UniquePlayersException extends RuntimeException {
    public UniquePlayersException() {
        super("All players should be unique");
    }
}