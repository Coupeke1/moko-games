package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class GameSizeException extends RuntimeException {
    public GameSizeException(int maxAmount) {
        super(String.format("Game should have between 2 and %d players", maxAmount));
    }
}