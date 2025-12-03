package be.kdg.team22.storeservice.domain.cart.exceptions;

import java.util.UUID;

public class InvalidMetadataException extends RuntimeException {
    public InvalidMetadataException(UUID gameId, String message) {
        super(String.format("Invalid metadata for game %s cause: %s", gameId, message));
    }
}