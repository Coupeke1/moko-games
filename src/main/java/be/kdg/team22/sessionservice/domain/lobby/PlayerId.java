package be.kdg.team22.sessionservice.domain.lobby;

import java.util.UUID;

public record PlayerId(UUID value) {
    public PlayerId {
        if (value == null) throw new IllegalArgumentException("PlayerId cannot be null");
    }

    public static PlayerId from(UUID uuid) {
        return new PlayerId(uuid);
    }

    public static PlayerId create(String value) {
        return new PlayerId(UUID.fromString(value));
    }
}