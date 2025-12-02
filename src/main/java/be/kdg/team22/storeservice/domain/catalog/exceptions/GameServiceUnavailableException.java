package be.kdg.team22.storeservice.domain.catalog.exceptions;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class GameServiceUnavailableException extends RuntimeException {
    public GameServiceUnavailableException() {
        super("Games-service is currently unreachable");
    }
}
