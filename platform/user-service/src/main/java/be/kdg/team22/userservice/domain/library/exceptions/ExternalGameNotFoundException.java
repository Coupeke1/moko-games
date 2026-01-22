package be.kdg.team22.userservice.domain.library.exceptions;

import java.util.UUID;

public class ExternalGameNotFoundException extends RuntimeException {
    public ExternalGameNotFoundException(UUID gameId) {
        super(String.format("External game with id '%s' was not found", gameId));
    }

    public ExternalGameNotFoundException(String name) {
        super(String.format("External game with name '%s' was not found", name));
    }
}
