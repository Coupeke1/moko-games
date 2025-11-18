package be.kdg.team22.userservice.profile.api.profile;

import be.kdg.team22.userservice.profile.domain.profile.Profile;

import java.time.Instant;
import java.util.UUID;

public record ProfileModel(
        UUID id,
        String username,
        String email,
        Instant createdAt
) {
    public static ProfileModel from(Profile profile) {
        return new ProfileModel(
                profile.id().value(),
                profile.username(),
                profile.email(),
                profile.createdAt()
        );
    }
}
