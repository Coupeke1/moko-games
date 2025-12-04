package be.kdg.team22.storeservice.infrastructure.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ExternalPaymentRepository {

    private final WebClient client;

    public ExternalPaymentRepository(@Value("${mollie.api-key}") String apiKey) {
        this.client = WebClient.builder()
                .baseUrl("https://api.mollie.com/v2")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public MolliePaymentResponse createPayment(BigDecimal amount,
                                               String redirectUrl,
                                               String webhookUrl,
                                               String description) {
        return client.post()
                .uri("/payments")
                .bodyValue(Map.of(
                        "amount", Map.of(
                                "currency", "EUR",
                                "value", amount.setScale(2).toString()
                        ),
                        "description", description,
                        "redirectUrl", redirectUrl,
                        "webhookUrl", webhookUrl
                ))
                .retrieve()
                .bodyToMono(MolliePaymentResponse.class)
                .block();
    }

    public MolliePaymentStatusResponse getPayment(String paymentId) {
        return client.get()
                .uri("/payments/" + paymentId)
                .retrieve()
                .bodyToMono(MolliePaymentStatusResponse.class)
                .block();
    }
}
