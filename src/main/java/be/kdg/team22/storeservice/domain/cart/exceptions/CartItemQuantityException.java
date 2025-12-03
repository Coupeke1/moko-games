package be.kdg.team22.storeservice.domain.cart.exceptions;

public class CartItemQuantityException extends RuntimeException {
    public CartItemQuantityException(int qty) {
        super("Quantity must be positive, but was " + qty);
    }
}