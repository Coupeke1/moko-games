package be.kdg.team22.checkersservice.domain.game.exceptions;

import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public class NotPlayersTurnException extends RuntimeException {
    public NotPlayersTurnException(final PlayerRole currentRole) {
        super(String.format("Cannot make move during player %s's turn", currentRole.name()));
    }
}