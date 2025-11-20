package be.kdg.team22.sessionservice.domain.lobby;

import java.util.UUID;

public record LobbyId(UUID value) {
    public LobbyId {
        if (value == null) throw new IllegalArgumentException("LobbyId cannot be null");
    }

    public static LobbyId from(UUID uuid) {
        return new LobbyId(uuid);
    }

    public static LobbyId create(String value) {
        return new LobbyId(UUID.fromString(value));
    }

    public static LobbyId newId() {
        return new LobbyId(UUID.randomUUID());
    }
}