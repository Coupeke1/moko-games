package be.kdg.team22.socialservice.infrastructure.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ExternalUserRepositoryConfig {
    @Bean("userService")
    RestClient productCatalogRestTemplate(@Value("${user-service.url}") final String url) {
        return RestClient.create(url);
    }
}
