package be.kdg.team22.storeservice.domain.cart.exceptions;

import java.util.UUID;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(UUID userId) {
        super("No cart exists for user: " + userId);
    }
}