package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record LobbyId(UUID value) {
    public LobbyId {
        if (value == null)
            throw new IllegalArgumentException("LobbyId cannot be null");
    }

    public static LobbyId from(UUID value) {
        return new LobbyId(value);
    }

    public static LobbyId create() {
        return new LobbyId(UUID.randomUUID());
    }

    public static LobbyId create(String value) {
        return new LobbyId(UUID.fromString(value));
    }

    public LobbyNotFoundException notFound() {
        throw new LobbyNotFoundException(this);
    }
}