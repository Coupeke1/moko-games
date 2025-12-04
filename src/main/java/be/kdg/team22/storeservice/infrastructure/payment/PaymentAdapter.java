package be.kdg.team22.storeservice.infrastructure.payment;

import be.kdg.team22.storeservice.domain.order.Order;
import be.kdg.team22.storeservice.domain.payment.Payment;
import be.kdg.team22.storeservice.domain.payment.PaymentProvider;
import be.kdg.team22.storeservice.domain.payment.exceptions.PaymentProviderException;
import com.mollie.mollie.Client;
import com.mollie.mollie.models.components.*;
import com.mollie.mollie.models.operations.CreatePaymentResponse;
import com.mollie.mollie.models.operations.GetPaymentRequest;
import com.mollie.mollie.models.operations.GetPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Component
public class PaymentAdapter implements PaymentProvider {
    private final Client client;
    private final String redirectBase;
    private final String webhookUrl;

    public PaymentAdapter(
            @Value("${mollie.api-key}") String apiKey,
            @Value("${frontend-url}") String redirectBase,
            @Value("${mollie.webhook-url:}") String webhookUrl
    ) {
        this.client = Client.builder()
                .security(Security.builder().apiKey(apiKey).build())
                .build();

        this.redirectBase = redirectBase;
        this.webhookUrl = webhookUrl;
    }

    @Override
    public Payment createPayment(Order order) {
        try {
            PaymentRequest request = PaymentRequest.builder()
                    .description("Order " + order.id().value())
                    .amount(Amount.builder()
                            .currency("EUR")
                            .value(order.totalPrice().setScale(2, RoundingMode.HALF_UP).toString())
                            .build())
                    .redirectUrl(redirectBase + "/payment/success?orderId=" + order.id().value())
                    .build();

            CreatePaymentResponse response = client.payments().create()
                    .paymentRequest(request)
                    .call();

            PaymentResponse paymentRes = response.paymentResponse().orElseThrow(PaymentProviderException::new);
            String paymentId = paymentRes.id().orElseThrow(PaymentProviderException::new);
            String checkoutUrl = paymentRes.links()
                    .orElseThrow(PaymentProviderException::new)
                    .checkout()
                    .orElseThrow(PaymentProviderException::new)
                    .href();

            return new Payment(paymentId, checkoutUrl);

        } catch (Exception ex) {
            throw new PaymentProviderException(ex);
        }
    }


    @Override
    public boolean isPaymentPaid(String paymentId) {
        GetPaymentRequest req = GetPaymentRequest.builder()
                .paymentId(paymentId)
                .build();

        GetPaymentResponse response = client.payments().get(req);

        PaymentResponse payment = response.paymentResponse().orElseThrow();
        PaymentStatus status = payment.status().orElseThrow();

        return status.equals(PaymentStatus.PAID);
    }

    @Override
    public PaymentStatus getPaymentStatus(String paymentId) {
        GetPaymentRequest req = GetPaymentRequest.builder()
                .paymentId(paymentId)
                .build();

        GetPaymentResponse response = client.payments().get(req);
        PaymentResponse payment = response.paymentResponse().orElseThrow();

        return payment.status().orElseThrow();
    }
}