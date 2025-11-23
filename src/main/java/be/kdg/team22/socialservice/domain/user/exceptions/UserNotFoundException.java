package be.kdg.team22.socialservice.domain.user.exceptions;

import be.kdg.team22.socialservice.domain.user.UserId;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UserId id) {
        super(String.format("Profile with id '%s' was not found", id));
    }

    public UserNotFoundException(String username) {
        super(String.format("Profile with username '%s' was not found", username));
    }
}
