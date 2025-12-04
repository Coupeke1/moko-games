package be.kdg.team22.storeservice.domain.order.exceptions;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String paymentId) {
        super("Cannot find payment with id: " + paymentId);
    }
}