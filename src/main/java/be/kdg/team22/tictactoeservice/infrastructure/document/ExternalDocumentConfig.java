package be.kdg.team22.tictactoeservice.infrastructure.document;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalDocumentConfig {

    @Bean
    @Qualifier("documentService")
    RestClient botServiceClient(@Value("${business.bot-service.url}") final String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}