package be.kdg.team22.storeservice.domain.catalog;

import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record GameId(UUID value) {
    public static GameId create() {
        return new GameId(UUID.randomUUID());
    }

    public static GameId from(UUID value) {
        return new GameId(value);
    }

    public GameNotFoundException notFound() {
        return new GameNotFoundException(this);
    }
}