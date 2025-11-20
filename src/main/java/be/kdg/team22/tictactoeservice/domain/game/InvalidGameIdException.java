package be.kdg.team22.tictactoeservice.domain.game;

public class InvalidGameIdException extends RuntimeException {
    public InvalidGameIdException() {
        super("Game ID is invalid");
    }
}
