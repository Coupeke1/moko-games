package be.kdg.team22.sessionservice.infrastructure.games;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpGameClient implements GameClient {

    private final RestClient restClient;

    public HttpGameClient(@Qualifier("gameService") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public StartGameResponse startGame(StartGameRequest request, Jwt token) {
        return restClient.post()
                .uri("/api/games")
                .header("Authorization", "Bearer " + token.getTokenValue())
                .body(request)
                .retrieve()
                .body(StartGameResponse.class);
    }
}