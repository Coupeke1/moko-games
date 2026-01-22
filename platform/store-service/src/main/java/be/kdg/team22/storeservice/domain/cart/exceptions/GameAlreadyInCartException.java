package be.kdg.team22.storeservice.domain.cart.exceptions;

import java.util.UUID;

public class GameAlreadyInCartException extends RuntimeException {
    public GameAlreadyInCartException(UUID gameId, UUID userId) {
        super("Game " + gameId + " is already in cart for user " + userId);
    }
}