package be.kdg.team22.gameaclservice.infrastructure.external.chess;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalChessGameConfig {

    @Bean
    @Qualifier("chessGame")
    RestClient chessGameRestClient(
            @Value("${acl-config.chess-info.backend-url}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl + "/api/games")
                .build();
    }
}
