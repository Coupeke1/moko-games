package be.kdg.team22.socialservice.application.friends.exceptions;

import be.kdg.team22.socialservice.domain.friends.user.UserId;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Friendship was not found");
    }

    private NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException betweenFriendship(UserId currentUser, UserId other) {
        return new NotFoundException(String.format("No friendship found between %s and %s", currentUser.value(), other.value()));
    }
}
