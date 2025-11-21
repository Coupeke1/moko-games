package be.kdg.team22.tictactoeservice.domain.game.exceptions;

import java.util.UUID;

public class NotPlayersTurnException extends RuntimeException {
    public NotPlayersTurnException(final UUID currentPlayer) {
        super(String.format("Cannot make move during player %s's turn", currentPlayer));
    }
}
