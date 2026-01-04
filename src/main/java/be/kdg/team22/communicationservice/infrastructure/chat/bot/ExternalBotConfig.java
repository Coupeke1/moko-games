package be.kdg.team22.communicationservice.infrastructure.chat.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalBotConfig {
    @Bean("botService")
    RestClient botRestClient(
            @Value("${business.chat-service.url}") final String baseUrl,
            @Value("${business.chat-service.api-key}") final String apiKey) {
        return createRestClient(baseUrl, apiKey);
    }

    @Bean("botServiceFallback")
    RestClient botRestClientFallback(
            @Value("${business.chat-service.fallback_url}") final String fallbackUrl,
            @Value("${business.chat-service.api-key}") final String apiKey) {
        return createRestClient(fallbackUrl, apiKey);
    }

    private RestClient createRestClient(final String baseUrl, final String apiKey) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2000);
        requestFactory.setReadTimeout(20000);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeaders(headers -> {
                    if (apiKey != null && !apiKey.isEmpty())
                        headers.set("X-API-Key", apiKey);
                })
                .build();
    }
}