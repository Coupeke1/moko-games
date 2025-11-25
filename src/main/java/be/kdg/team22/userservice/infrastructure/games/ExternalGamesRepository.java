package be.kdg.team22.userservice.infrastructure.games;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class ExternalGamesRepository {
    private final RestClient client;

    public ExternalGamesRepository(RestClient gameServiceRestClient) {
        this.client = gameServiceRestClient;
    }

    public GameDetailsResponse getGame(UUID gameId, Jwt token) {
        return client.get()
                .uri("/{id}", gameId)
                .header("Authorization", "Bearer " + token.getTokenValue())
                .retrieve()
                .body(GameDetailsResponse.class);
    }
}