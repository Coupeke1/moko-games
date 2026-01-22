package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.GameIdNullException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record GameId(UUID value) {

    public GameId {
        if (value == null) throw new GameIdNullException();
    }

    public static GameId from(UUID value) {
        return new GameId(value);
    }

    public static GameId from(String value) {
        return new GameId(UUID.fromString(value));
    }

    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }

    public GameNotFoundException notFound() {
        return new GameNotFoundException(this);
    }
}
