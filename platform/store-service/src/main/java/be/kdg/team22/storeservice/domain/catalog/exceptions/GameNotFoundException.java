package be.kdg.team22.storeservice.domain.catalog.exceptions;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(final GameId id) {
        super("Game with id '%s' not found".formatted(id));
    }
}
