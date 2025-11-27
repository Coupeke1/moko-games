package be.kdg.team22.tictactoeservice.infrastructure.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalAiRepositoryConfig {

    @Bean("aiService")
    RestClient aiRestTemplate(
            @Value("${ai.url}") final String url,
            RestClient.Builder builder) {  // Inject the builder
        return builder
                .baseUrl(url)
                .build();
    }
}
