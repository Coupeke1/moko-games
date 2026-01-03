package be.kdg.team22.communicationservice.domain.notification.exceptions;

import be.kdg.team22.communicationservice.domain.chat.UserId;

public class UserProfileNotFoundException extends RuntimeException {
    public UserProfileNotFoundException(UserId id) {
        super(String.format("User with id '%s' was not found", id.value()));
    }
}