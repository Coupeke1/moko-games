package be.kdg.team22.socialservice.domain.friends.friendship.exceptions;

import be.kdg.team22.socialservice.domain.friends.user.Username;

public class AlreadyFriendsException extends RuntimeException {
    public AlreadyFriendsException(Username username) {
        super(String.format("You are already friends with user '%s'", username));
    }
}
