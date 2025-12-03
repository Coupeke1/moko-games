package be.kdg.team22.checkersservice.domain.game.exceptions;

import be.kdg.team22.checkersservice.domain.player.PlayerRole;

public class RoleUnfulfilledException extends RuntimeException {
    public RoleUnfulfilledException(final PlayerRole role) {
        super(String.format("Role '%s' is not assigned in the current game", role.name()));
    }

}