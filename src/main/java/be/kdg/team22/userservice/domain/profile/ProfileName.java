package be.kdg.team22.userservice.domain.profile;

import be.kdg.team22.userservice.domain.profile.exceptions.NotFoundException;

public record ProfileName(String value) {
    public static ProfileName create(String value) {
        return new ProfileName(value.trim());
    }

    public NotFoundException notFound() {
        throw new NotFoundException(value);
    }
}