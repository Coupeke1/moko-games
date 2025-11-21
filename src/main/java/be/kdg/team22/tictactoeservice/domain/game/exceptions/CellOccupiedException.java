package be.kdg.team22.tictactoeservice.domain.game.exceptions;

public class CellOccupiedException extends RuntimeException {
    public CellOccupiedException(final int row, final int col) {
        super(String.format("Cell [%d,%d] is already occupied", row, col));
    }
}
