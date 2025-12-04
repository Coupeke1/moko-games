package be.kdg.team22.storeservice.domain.order.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Cannot find the order");
    }
}