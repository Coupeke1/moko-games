package be.kdg.team22.socialservice.domain.friendship.exceptions;

import be.kdg.team22.socialservice.domain.user.Username;

public class AlreadyFriendsException extends RuntimeException {
    public AlreadyFriendsException(Username username) {
        super(String.format("You are already friends with user '%s'", username));
    }
}
