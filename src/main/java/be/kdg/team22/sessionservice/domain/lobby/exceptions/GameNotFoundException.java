package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(UUID gameId) {
        super("Game with id '%s' could not be started: not found".formatted(gameId));
    }
}
