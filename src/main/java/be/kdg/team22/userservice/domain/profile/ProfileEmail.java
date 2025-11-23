package be.kdg.team22.userservice.domain.profile;

import be.kdg.team22.userservice.domain.profile.exceptions.ProfileNotFoundException;

public record ProfileEmail(String value) {
    public static ProfileEmail create(String value) {
        return new ProfileEmail(value.trim());
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