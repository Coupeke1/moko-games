package be.kdg.team22.socialservice.domain.friends.user.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(UUID id) {
        super(String.format("Profile with id '%s' was not found", id));
    }

    public NotFoundException(String username) {
        super(String.format("Profile with username '%s' was not found", username));
    }
}
