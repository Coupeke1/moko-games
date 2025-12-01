package be.kdg.team22.tictactoeservice.domain.player.exceptions;

import java.util.UUID;

public class InvalidPlayerException extends RuntimeException {
    public InvalidPlayerException(final UUID id) {
        super(String.format("Player with value '%s' is invalid", id));
    }

    public InvalidPlayerException() {
        super("Player is invalid");
    }
}