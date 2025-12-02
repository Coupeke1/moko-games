package be.kdg.team22.storeservice.infrastructure.games;

import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExternalGamesRepository {

    private final RestClient client;

    public ExternalGamesRepository(@Qualifier("gamesService") RestClient client) {
        this.client = client;
    }

    public GameMetadataResponse fetchMetadata(UUID gameId) {
        try {
            GameMetadataResponse response = client.get()
                    .uri("/" + gameId)
                    .retrieve()
                    .body(GameMetadataResponse.class);

            if (response == null)
                throw new GameNotFoundException(gameId);

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new GameNotFoundException(gameId);
            throw e;
        } catch (RestClientException e) {
            throw new GameServiceUnavailableException();
        }
    }
}