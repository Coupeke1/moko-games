package be.kdg.team22.storeservice.domain.cart.exceptions;

import java.util.UUID;

public class GameAlreadyOwnedException extends RuntimeException {
    public GameAlreadyOwnedException(UUID gameId, UUID userId) {
        super("User " + userId + " already owns game " + gameId);
    }
}
