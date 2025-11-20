package be.kdg.team22.socialservice.domain.friends.friendship.exceptions;

import be.kdg.team22.socialservice.domain.friends.user.UserId;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(UUID id) {
        super(String.format("Friendship with id '%s' was not found", id));
    }

    private NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException betweenFriendship(UserId currentUser, UserId other) {
        return new NotFoundException(String.format("No friendship found between %s and %s", currentUser.value(), other.value()));
    }
}
