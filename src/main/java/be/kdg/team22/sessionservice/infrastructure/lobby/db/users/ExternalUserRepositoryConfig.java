package be.kdg.team22.sessionservice.infrastructure.lobby.db.users;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ExternalUserRepositoryConfig {

    @Bean
    @Qualifier("userService")
    RestClient userServiceClient(
            @Value("${user-service.url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}