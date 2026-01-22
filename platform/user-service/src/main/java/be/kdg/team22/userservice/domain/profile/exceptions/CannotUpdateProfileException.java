package be.kdg.team22.userservice.domain.profile.exceptions;

import be.kdg.team22.userservice.domain.profile.ProfileId;

public class CannotUpdateProfileException extends RuntimeException {
    public CannotUpdateProfileException(ProfileId id, String attribute) {
        super(String.format("'%s' could not be updated on profile with id '%s'", attribute, id.value()));
    }

    public static CannotUpdateProfileException description(ProfileId id) {
        return new CannotUpdateProfileException(id, "description");
    }

    public static CannotUpdateProfileException image(ProfileId id) {
        return new CannotUpdateProfileException(id, "image");
    }

    public static CannotUpdateProfileException modules(ProfileId id) {
        return new CannotUpdateProfileException(id, "modules");
    }

    public static CannotUpdateProfileException notifications(ProfileId id) {
        return new CannotUpdateProfileException(id, "notifications");
    }

    public static CannotUpdateProfileException levels(ProfileId id) {
        return new CannotUpdateProfileException(id, "levels");
    }
}