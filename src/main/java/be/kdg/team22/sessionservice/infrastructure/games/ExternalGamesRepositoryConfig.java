package be.kdg.team22.sessionservice.infrastructure.games;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ExternalGamesRepositoryConfig {
    @Bean
    @Qualifier("gameService")
    RestClient gameServiceClient(@Value("${business.game-service.url}") final String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl + "/api/games")
                .build();
    }
}