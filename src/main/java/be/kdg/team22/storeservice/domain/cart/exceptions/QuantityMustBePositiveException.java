package be.kdg.team22.storeservice.domain.cart.exceptions;

public class QuantityMustBePositiveException extends RuntimeException {
    public QuantityMustBePositiveException(int qty) {
        super("Quantity must be positive, but was " + qty);
    }
}