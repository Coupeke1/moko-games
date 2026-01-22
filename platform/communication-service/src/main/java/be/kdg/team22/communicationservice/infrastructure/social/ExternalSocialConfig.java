package be.kdg.team22.communicationservice.infrastructure.social;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalSocialConfig {
    @Bean
    @Qualifier("socialService")
    RestClient socialServiceClient(@Value("${business.social-service.url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}

