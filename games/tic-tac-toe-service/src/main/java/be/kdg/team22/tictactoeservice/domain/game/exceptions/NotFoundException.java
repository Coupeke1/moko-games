package be.kdg.team22.tictactoeservice.domain.game.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final UUID id) {
        super(String.format("Game with value '%s' was not found", id));
    }
}
