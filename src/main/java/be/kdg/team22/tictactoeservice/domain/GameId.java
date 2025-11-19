package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.util.Assert;

import java.util.UUID;

@ValueObject
public record GameId(UUID id) {
    public GameId {
        Assert.notNull(id, "Game id must not be null");
    }

    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }

    public static GameId fromString(String id) {
        return new GameId(UUID.fromString(id));
    }
}
