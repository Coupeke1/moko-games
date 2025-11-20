package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(UUID id) {
        super(String.format("Owner with id '%s' was not found in players", id));
    }
}