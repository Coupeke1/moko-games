package be.kdg.team22.communicationservice.infrastructure.lobby;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalLobbyRepositoryConfig {
    @Bean
    @Qualifier("sessionService")
    RestClient sessionServiceClient(@Value("${business.session-service.url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}