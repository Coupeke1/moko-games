package be.kdg.team22.storeservice.domain.cart.exceptions;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(UUID gameId) {
        super("Cart does not contain item with gameId: " + gameId);
    }
}