package be.kdg.team22.userservice.domain.profile;

import be.kdg.team22.userservice.domain.profile.exceptions.ProfileNotFoundException;

public record ProfileName(String value) {
    public static ProfileName create(String value) {
        return new ProfileName(value.trim());
    }

    public ProfileNotFoundException notFound() {
        throw new ProfileNotFoundException(this);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.trim();
    }
}