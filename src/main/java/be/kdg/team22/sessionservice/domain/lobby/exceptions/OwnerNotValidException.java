package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class OwnerNotValidException extends RuntimeException {
    public OwnerNotValidException(UUID ownerId) {
        super(String.format("Owner with id '%s' is not valid", ownerId));
    }
}
