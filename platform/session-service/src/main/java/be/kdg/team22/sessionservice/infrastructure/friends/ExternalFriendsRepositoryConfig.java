package be.kdg.team22.sessionservice.infrastructure.friends;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ExternalFriendsRepositoryConfig {
    @Bean
    @Qualifier("socialService")
    RestClient socialServiceClient(@Value("${business.social-service.url}") final String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl + "/api/friends")
                .build();
    }
}
