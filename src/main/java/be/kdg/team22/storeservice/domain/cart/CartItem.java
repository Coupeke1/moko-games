package be.kdg.team22.storeservice.domain.cart;

import be.kdg.team22.storeservice.domain.cart.exceptions.CartItemQuantityException;
import org.jmolecules.ddd.annotation.ValueObject;

import java.util.UUID;

@ValueObject
public record CartItem(UUID gameId, int quantity) {

    public CartItem {
        if (quantity <= 0) {
            throw new CartItemQuantityException(quantity);
        }
    }

    public CartItem add(int extra) {
        return new CartItem(gameId, quantity + extra);
    }
}