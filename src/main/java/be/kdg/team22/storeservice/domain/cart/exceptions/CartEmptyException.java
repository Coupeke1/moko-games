package be.kdg.team22.storeservice.domain.cart.exceptions;

import java.util.UUID;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException(UUID userId) {
        super("Cart is empty for user " + userId);
    }
}
