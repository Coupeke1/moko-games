package be.kdg.team22.sessionservice.infrastructure.games;

import be.kdg.team22.sessionservice.domain.game.GameRepository;
import be.kdg.team22.sessionservice.domain.game.GameServiceNotReachableException;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.GameNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.NotReachableException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalGamesRepository implements GameRepository {

    private final RestClient client;

    public ExternalGamesRepository(
        @Qualifier("gameService") final RestClient client
    ) {
        this.client = client;
    }

    public StartGameResponse startGame(StartGameRequest request, Jwt token) {
        try {
            return client
                .post()
                .uri("")
                .header("Authorization", "Bearer " + token.getTokenValue())
                .body(request)
                .retrieve()
                .body(StartGameResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new GameNotFoundException(request.gameId());
            }

            throw exception;
        } catch (RestClientException exception) {
            throw NotReachableException.GamesService();
        }
    }

    public String findGameNameById(final GameId gameId) {
        try {
            GameResponse response = client
                .get()
                .uri("/" + gameId.value())
                .retrieve()
                .body(GameResponse.class);

            if (response == null) throw new GameNotFoundException(
                gameId.value()
            );

            return response.name();
        } catch (HttpClientErrorException exception) {
            if (
                exception.getStatusCode() == HttpStatus.NOT_FOUND
            ) throw new GameNotFoundException(gameId.value());
            throw exception;
        } catch (RestClientException exception) {
            throw new GameServiceNotReachableException();
        }
    }

    public Map<String, Object> validateAndResolveSettings(
        GameId gameId,
        Map<String, Object> settings,
        Jwt token
    ) {
        try {
            ValidateGameSettingsResponse resp = client
                .post()
                .uri("/" + gameId.value() + "/settings/validate")
                .header("Authorization", "Bearer " + token.getTokenValue())
                .body(new ValidateGameSettingsRequest(settings))
                .retrieve()
                .body(ValidateGameSettingsResponse.class);

            if (resp == null) throw new GameNotFoundException(gameId.value());
            return resp.resolvedSettings() == null
                ? Map.of()
                : resp.resolvedSettings();
        } catch (HttpClientErrorException ex) {
            if (
                ex.getStatusCode() == HttpStatus.NOT_FOUND
            ) throw new GameNotFoundException(gameId.value());
            throw ex;
        } catch (RestClientException ex) {
            throw new GameServiceNotReachableException();
        }
    }
}
