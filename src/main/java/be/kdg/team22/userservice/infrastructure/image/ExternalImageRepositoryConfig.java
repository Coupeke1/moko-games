package be.kdg.team22.userservice.infrastructure.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ExternalImageRepositoryConfig {
    @Bean("catService")
    RestClient productCatalogRestTemplate(@Value("${business.cat-service.url}") final String url) {
        return RestClient.create(url);
    }
}
