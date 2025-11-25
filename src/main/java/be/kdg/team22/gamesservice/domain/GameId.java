package be.kdg.team22.gamesservice.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record GameId(UUID value) {
    public GameId {
        if (value == null) {
            throw new IllegalArgumentException("GameId value cannot be null");
        }
    }

    public static GameId fromString(String id) {
        return new GameId(UUID.fromString(id));
    }
}