package be.kdg.team22.sessionservice.domain.lobby;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record GameId(UUID value) {
    public GameId {
        if (value == null) throw new IllegalArgumentException("GameId cannot be null");
    }

    public static GameId from(UUID uuid) {
        return new GameId(uuid);
    }

    public static GameId create(String value) {
        return new GameId(UUID.fromString(value));
    }
}
