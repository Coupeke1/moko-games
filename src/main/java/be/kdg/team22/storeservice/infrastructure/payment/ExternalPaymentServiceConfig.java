package be.kdg.team22.storeservice.infrastructure.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalPaymentServiceConfig {

    @Bean
    @Qualifier("mollieService")
    RestClient mollieServiceClient(
            @Value("${mollie.api-key}") String apiKey
    ) {
        return RestClient.builder()
                .baseUrl("https://api.mollie.com/v2")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}