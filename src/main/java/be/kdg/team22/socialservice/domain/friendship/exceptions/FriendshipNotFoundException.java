package be.kdg.team22.socialservice.domain.friendship.exceptions;

import be.kdg.team22.socialservice.domain.user.UserId;

import java.util.UUID;

public class FriendshipNotFoundException extends RuntimeException {
    private FriendshipNotFoundException(String message) {
        super(message);
    }

    public FriendshipNotFoundException(UUID id) {
        super(String.format("Friendship with id '%s' was not found", id));
    }

    public static FriendshipNotFoundException betweenFriendship(UserId currentUser, UserId other) {
        return new FriendshipNotFoundException(String.format("No friendship found between %s and %s", currentUser.value(), other.value()));
    }
}
