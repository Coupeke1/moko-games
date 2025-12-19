package be.kdg.team22.gameaclservice.infrastructure.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalUserRepositoryConfig {

    @Bean
    @Qualifier("userService")
    RestClient userServiceRestClient(
            @Value("${business.user-service.url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl + "/api/profiles")
                .build();
    }
}
