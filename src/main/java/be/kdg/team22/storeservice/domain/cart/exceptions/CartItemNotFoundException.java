package be.kdg.team22.storeservice.domain.cart.exceptions;

import java.util.UUID;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(UUID gameId, UUID userId) {
        super("Game " + gameId + " is not in the cart of user " + userId);
    }
}