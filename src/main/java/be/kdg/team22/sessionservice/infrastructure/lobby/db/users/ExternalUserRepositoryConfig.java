package be.kdg.team22.sessionservice.infrastructure.lobby.db.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ExternalUserRepositoryConfig {

    @Bean("userService")
    RestClient userServiceRestClient(@Value("${user-service.url}") String url) {
        return RestClient.create(url);
    }
}
