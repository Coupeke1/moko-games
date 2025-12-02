package be.kdg.team22.checkersservice.domain.game.exceptions;

public class BoardSizeException extends RuntimeException {
    public BoardSizeException() {
        super("The board should be either 8x8 or 10x10");
    }
}
