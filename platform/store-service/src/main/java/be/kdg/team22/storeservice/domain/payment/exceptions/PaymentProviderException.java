package be.kdg.team22.storeservice.domain.payment.exceptions;

public class PaymentProviderException extends RuntimeException {

    public PaymentProviderException() {
        super("Payment provider failed");
    }

    public PaymentProviderException(Throwable cause) {
        super("Payment provider failed", cause);
    }
}