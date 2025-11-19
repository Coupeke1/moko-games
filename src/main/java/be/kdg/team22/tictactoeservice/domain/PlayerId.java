package be.kdg.team22.tictactoeservice.domain;

import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.util.Assert;

import java.util.UUID;

@ValueObject
public record PlayerId(UUID id) {
    public PlayerId {
        Assert.notNull(id, "Player id must not be null");
    }

    public static PlayerId create() {
        return new PlayerId(UUID.randomUUID());
    }
}
