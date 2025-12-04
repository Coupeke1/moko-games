package be.kdg.team22.storeservice.domain.payment;

import be.kdg.team22.storeservice.domain.order.Order;

public interface PaymentProvider {
    Payment createPayment(Order order);

    boolean isPaymentPaid(String paymentId);
}