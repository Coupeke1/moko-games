package be.kdg.team22.tictactoeservice.domain.player;

public class InvalidPlayerIdException extends RuntimeException {
    public InvalidPlayerIdException() {
        super("Player ID is invalid");
    }
}
