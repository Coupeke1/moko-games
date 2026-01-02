package be.kdg.team22.tictactoeservice.infrastructure.document;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalDocumentConfig {

    @Bean("documentService")
    RestClient documentRestClient(@Value("${business.chat-service.url}") final String baseUrl) {
        return createRestClient(baseUrl);
    }

    @Bean("documentServiceFallback")
    RestClient documentRestClientFallback(@Value("${business.chat-service.fallback_url}") final String fallbackUrl) {
        return createRestClient(fallbackUrl);
    }

    private RestClient createRestClient(String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}