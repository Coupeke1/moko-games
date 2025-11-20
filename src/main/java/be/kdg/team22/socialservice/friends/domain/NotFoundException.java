package be.kdg.team22.socialservice.friends.domain;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException betweenFriendship(UserId currentUser, UserId other) {
        return new NotFoundException(String.format("No friendship found between %s and %s", currentUser, other));
    }
}
