package be.kdg.team22.userservice.domain.profile.exceptions;

import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileName;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(ProfileId id) {
        super(String.format("Profile with id '%s' was not found", id));
    }

    public NotFoundException(ProfileName username) {
        super(String.format("Profile with username '%s' was not found", username));
    }
}
