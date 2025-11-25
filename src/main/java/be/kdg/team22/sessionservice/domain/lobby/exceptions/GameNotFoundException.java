package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID id) {
        super("Game with id '%s' could not be found".formatted(id));
    }
}
