package be.kdg.team22.storeservice.domain.order.exceptions;

public class PaymentIncompleteException extends RuntimeException {
    public PaymentIncompleteException() {
        super("Payment is incomplete.");
    }
}
