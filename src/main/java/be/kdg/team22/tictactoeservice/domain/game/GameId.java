package be.kdg.team22.tictactoeservice.domain.game;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record GameId(UUID id) {
    public GameId {
        if (id == null)
            throw new InvalidGameIdException();
    }

    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }

    public static GameId fromString(String id) {
        return new GameId(UUID.fromString(id));
    }
}
