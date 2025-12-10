package be.kdg.team22.tictactoeservice.domain.register.exceptions;

public class GameNotRegisteredException extends RuntimeException {
    public GameNotRegisteredException() {
        super("Game not registered");
    }
}
