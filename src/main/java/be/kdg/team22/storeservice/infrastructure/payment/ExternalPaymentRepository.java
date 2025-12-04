package be.kdg.team22.storeservice.infrastructure.payment;

import be.kdg.team22.storeservice.api.order.models.MolliePaymentResponse;
import be.kdg.team22.storeservice.api.order.models.MolliePaymentStatusResponse;
import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import be.kdg.team22.storeservice.domain.order.exceptions.PaymentNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class ExternalPaymentRepository {

    private final RestClient client;

    public ExternalPaymentRepository(@Qualifier("mollieService") RestClient client) {
        this.client = client;
    }

    public MolliePaymentResponse createPayment(BigDecimal amount,
                                               String redirectUrl,
                                               String webhookUrl,
                                               String description) {

        try {
            return client.post()
                    .uri("/payments")
                    .body(Map.of(
                            "amount", Map.of(
                                    "currency", "EUR",
                                    "value", amount.setScale(2, RoundingMode.HALF_UP).toString()
                            ),
                            "description", description,
                            "redirectUrl", redirectUrl,
                            "webhookUrl", webhookUrl
                    ))
                    .retrieve()
                    .body(MolliePaymentResponse.class);

        } catch (RestClientException e) {
            throw ServiceUnavailableException.PaymentProviderUnavailable();
        }
    }

    public MolliePaymentStatusResponse getPayment(String paymentId) {
        try {
            return client.get()
                    .uri("/payments/{id}", paymentId)
                    .retrieve()
                    .body(MolliePaymentStatusResponse.class);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new PaymentNotFoundException(paymentId);
            throw e;
        } catch (RestClientException e) {
            throw ServiceUnavailableException.PaymentProviderUnavailable();
        }
    }
}