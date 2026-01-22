package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class BoardSizeException extends RuntimeException {
    public BoardSizeException(final int min, final int max) {
        super(String.format("Board size should be between %d and %d", min, max));
    }
}