package be.kdg.team22.userservice.infrastructure.games;

import be.kdg.team22.userservice.domain.achievement.AchievementKey;
import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.library.exceptions.ExternalAchievementNotFoundException;
import be.kdg.team22.userservice.domain.library.exceptions.ExternalGameNotFoundException;
import be.kdg.team22.userservice.domain.library.exceptions.GameServiceNotReachableException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExternalGamesRepository {
    private final RestClient client;

    public ExternalGamesRepository(RestClient gameServiceRestClient) {
        this.client = gameServiceRestClient;
    }

    public GameDetailsResponse getGame(UUID gameId, Jwt token) {
        try {
            return client.get()
                    .uri("/{id}", gameId)
                    .header("Authorization", "Bearer " + token.getTokenValue())
                    .retrieve()
                    .body(GameDetailsResponse.class);

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ExternalGameNotFoundException(gameId);
            }

            throw ex;
        } catch (RestClientException ex) {
            throw new GameServiceNotReachableException(client.toString());
        }
    }

    public GameDetailsResponse getGame(final String name) {
        try {
            return client.get()
                    .uri("/{name}", name)
                    .retrieve()
                    .body(GameDetailsResponse.class);

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ExternalGameNotFoundException(name);
            }

            throw ex;
        } catch (RestClientException ex) {
            throw new GameServiceNotReachableException(client.toString());
        }
    }

    public AchievementDetailsResponse getAchievementDetails(GameId gameId, AchievementKey achievementKey) {
        try {
            return client.get()
                    .uri("/{id}/achievements/{key}", gameId.value(), achievementKey.key())
                    .retrieve()
                    .body(AchievementDetailsResponse.class);

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ExternalAchievementNotFoundException(gameId.value(), achievementKey.key());
            }

            throw ex;
        } catch (RestClientException ex) {
            throw new GameServiceNotReachableException(client.toString());
        }
    }
}