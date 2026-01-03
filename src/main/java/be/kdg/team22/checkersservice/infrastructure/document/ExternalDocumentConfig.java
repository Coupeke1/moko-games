package be.kdg.team22.checkersservice.infrastructure.document;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class ExternalDocumentConfig {

    @Bean("documentService")
    RestClient documentRestClient(@Value("${business.chat.url}") final String baseUrl,
                                  @Value("${business.ai.api-key}") final String apiKey
    ) {
        return createRestClient(baseUrl, apiKey);
    }

    @Bean("documentServiceFallback")
    RestClient documentRestClientFallback(@Value("${business.chat.fallback_url}") final String fallbackUrl,
                                          @Value("${business.ai.api-key}") final String apiKey
    ) {
        return createRestClient(fallbackUrl, apiKey);
    }

    private RestClient createRestClient(String baseUrl, String apiKey) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(30000);
        requestFactory.setReadTimeout(30000);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                    if (apiKey != null && !apiKey.isBlank()) {
                        headers.set("X-API-Key", apiKey);
                    }
                })
                .build();
    }
}