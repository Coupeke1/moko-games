package be.kdg.team22.sessionservice.infrastructure.games;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.NotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalGamesRepository {
    private final RestClient client;

    public ExternalGamesRepository(@Qualifier("gameService") final RestClient client) {
        this.client = client;
    }

    public StartGameResponse startGame(StartGameRequest request, Jwt token) {
        try {
            return client.post().uri("/api/games").header("Authorization", "Bearer " + token.getTokenValue()).body(request).retrieve().body(StartGameResponse.class);
        } catch (
                HttpClientErrorException exception) {

            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new GameNotFoundException(request.gameId());
            }

            throw exception;
        } catch (RestClientException exception) {
            throw new NotReachableException();
        }
    }
}