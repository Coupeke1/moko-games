package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class InvalidCellException extends RuntimeException {
    public InvalidCellException(final int boardSize) {
        super(String.format("Cell position should be between 0 and %d", boardSize - 1));
    }
}
