package be.kdg.team22.tictactoeservice.infrastructure.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalAiRepositoryConfig {

    @Bean("aiService")
    RestClient aiRestClient(@Value("${ai.url}") final String url) {
        return RestClient.builder()
                .baseUrl(url)
                .defaultHeaders(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
                })
                .build();
    }
}