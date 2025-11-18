package be.kdg.team22.userservice.profile.domain.profile;

import java.util.UUID;

public record ProfileId(UUID value) {
    public ProfileId {
        if (value == null) throw new IllegalArgumentException("ProfileId cannot be null");
    }

    public static ProfileId from(UUID uuid) {
        return new ProfileId(uuid);
    }
}
