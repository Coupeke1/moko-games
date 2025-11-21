package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class GameSizeException extends RuntimeException {
    public GameSizeException() {
        super("Game should have a minimum of 2 players");
    }
}