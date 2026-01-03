package be.kdg.team22.communicationservice.infrastructure.chat.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalBotConfig {
    @Bean("botService")
    RestClient botRestClient(@Value("${business.chat-service.url}") final String baseUrl) {
        return createRestClient(baseUrl);
    }

    @Bean("botServiceFallback")
    RestClient botRestClientFallback(@Value("${business.chat-service.fallback_url}") final String fallbackUrl) {
        return createRestClient(fallbackUrl);
    }

    private RestClient createRestClient(String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2000);
        requestFactory.setReadTimeout(20000);

        return RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
    }
}