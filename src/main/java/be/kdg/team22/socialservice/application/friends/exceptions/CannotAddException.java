package be.kdg.team22.socialservice.application.friends.exceptions;

import be.kdg.team22.socialservice.domain.friends.user.Username;

public class CannotAddException extends RuntimeException {
    public CannotAddException(Username username) {
        super(String.format("Cannot add user '%s' as a friend", username));
    }
}
