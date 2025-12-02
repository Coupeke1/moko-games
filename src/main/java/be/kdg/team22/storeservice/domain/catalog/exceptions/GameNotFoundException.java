package be.kdg.team22.storeservice.domain.catalog.exceptions;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(UUID id) {
        super("Game with ID %s not found".formatted(id));
    }
}
