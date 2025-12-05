package be.kdg.team22.checkersservice.domain.move.exceptions;

import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public class BackwardsMoveException extends RuntimeException {
    public BackwardsMoveException(final PlayerRole color) {
        super(color + " pieces must move forward: " + (PlayerRole.BLACK.equals(color) ? "up" : "down"));
    }
}
