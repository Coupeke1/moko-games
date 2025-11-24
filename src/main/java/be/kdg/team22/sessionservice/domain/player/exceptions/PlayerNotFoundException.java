package be.kdg.team22.sessionservice.domain.player.exceptions;

import be.kdg.team22.sessionservice.domain.player.PlayerId;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(PlayerId id) {
        super(String.format("Player with id '%s' was not found", id));
    }

    public PlayerNotFoundException(String username) {
        super(String.format("Player with username '%s' was not found", username));
    }
}
