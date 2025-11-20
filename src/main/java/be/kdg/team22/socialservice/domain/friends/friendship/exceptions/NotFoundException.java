package be.kdg.team22.socialservice.domain.friends.friendship.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(UUID id) {
        super(String.format("Friendship with id '%s' was not found", id));
    }
}
