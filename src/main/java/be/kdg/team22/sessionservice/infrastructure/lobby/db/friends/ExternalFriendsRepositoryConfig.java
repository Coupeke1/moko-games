package be.kdg.team22.sessionservice.infrastructure.lobby.db.friends;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
class ExternalFriendsRepositoryConfig {

    @Bean("socialService")
    RestClient socialServiceRestClient(@Value("${social-service.url}") String url) {
        return RestClient.create(url);
    }
}