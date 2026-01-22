package be.kdg.team22.sessionservice.infrastructure.chat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalChatConfig {

    @Bean
    @Qualifier("communicationService")
    RestClient communicationClient(@Value("${business.communication-service.url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}