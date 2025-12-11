package be.kdg.team22.gameaclservice.domain.profile;

import java.util.UUID;

public record ProfileId(UUID value) {
    public static ProfileId create() {
        return new ProfileId(UUID.randomUUID());
    }

    public static ProfileId create(String value) {
        return new ProfileId(UUID.fromString(value));
    }

    public ProfileNotFoundException notFound() {
        throw new ProfileNotFoundException(this);
    }

}
