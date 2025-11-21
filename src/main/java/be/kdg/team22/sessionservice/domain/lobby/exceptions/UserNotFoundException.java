package be.kdg.team22.sessionservice.domain.lobby.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super(String.format("User with id '%s' was not found in user-service", id));
    }
}
