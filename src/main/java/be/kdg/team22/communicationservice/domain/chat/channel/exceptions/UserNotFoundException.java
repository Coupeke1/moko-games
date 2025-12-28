package be.kdg.team22.communicationservice.domain.chat.channel.exceptions;

import be.kdg.team22.communicationservice.domain.chat.UserId;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final UserId id) {
        super(String.format("User with id '%s' was not found", id.value()));
    }
}