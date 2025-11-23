package be.kdg.team22.userservice.domain.profile.exceptions;

import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileName;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(ProfileId id) {
        super(String.format("Profile with id '%s' was not found", id));
    }

    public ProfileNotFoundException(ProfileName username) {
        super(String.format("Profile with username '%s' was not found", username));
    }
}