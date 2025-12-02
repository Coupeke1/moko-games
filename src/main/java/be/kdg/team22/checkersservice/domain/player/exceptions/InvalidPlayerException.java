package be.kdg.team22.checkersservice.domain.player.exceptions;

public class InvalidPlayerException extends RuntimeException {
    public InvalidPlayerException() {
        super("Player is invalid");
    }
}