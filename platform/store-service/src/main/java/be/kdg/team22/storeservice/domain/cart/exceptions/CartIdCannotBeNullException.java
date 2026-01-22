package be.kdg.team22.storeservice.domain.cart.exceptions;

public class CartIdCannotBeNullException extends RuntimeException {
    public CartIdCannotBeNullException() {
        super("CartId cannot be null");
    }
}
