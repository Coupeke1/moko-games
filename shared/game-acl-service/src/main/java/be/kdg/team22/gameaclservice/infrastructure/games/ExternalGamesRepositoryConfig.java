package be.kdg.team22.gameaclservice.infrastructure.games;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalGamesRepositoryConfig {

    @Bean
    @Qualifier("gameService")
    RestClient gameServiceRestClient(
            @Value("${business.game-service.url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl + "/api/games")
                .build();
    }
}
