package be.kdg.team22.tictactoeservice.domain.player;

import be.kdg.team22.tictactoeservice.domain.player.exceptions.InvalidPlayerException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record PlayerId(UUID id) {
    public PlayerId {
        if (id == null)
            throw new InvalidPlayerException();
    }

    public static PlayerId create() {
        return new PlayerId(UUID.randomUUID());
    }
}