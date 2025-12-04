package be.kdg.team22.storeservice.domain.order.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Cannot find the order");
    }

    public OrderNotFoundException(String paymentId) {
        super("Cannot find the order for payment: " + paymentId);
    }
}