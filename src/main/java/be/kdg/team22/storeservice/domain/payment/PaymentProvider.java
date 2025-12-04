package be.kdg.team22.storeservice.domain.payment;

import be.kdg.team22.storeservice.domain.order.Order;
import com.mollie.mollie.models.components.PaymentStatus;

public interface PaymentProvider {
    Payment createPayment(Order order);

    boolean isPaymentPaid(String paymentId);

    PaymentStatus getPaymentStatus(String paymentId);

}