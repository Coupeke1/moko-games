package be.kdg.team22.storeservice.domain.order.exceptions;

public class OrderNotOwnedException extends RuntimeException {
    public OrderNotOwnedException(String message) {
        super(message);
    }
}
