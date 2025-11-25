package be.kdg.team22.userservice.infrastructure.games;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalGamesRepositoryConfig {

    @Bean
    RestClient gameServiceRestClient(
            @Value("${game-service.url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl + "/api/games")
                .build();
    }
}
