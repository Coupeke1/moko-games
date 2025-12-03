package be.kdg.team22.storeservice.domain.order.exceptions;

public class OrderEmptyException extends RuntimeException {
    public OrderEmptyException() {
        super("Cannot create an empty order");
    }
}