package be.kdg.team22.storeservice.infrastructure.games;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.catalog.exceptions.GameNotFoundException;
import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalGamesRepository {

    private final RestClient client;

    public ExternalGamesRepository(@Qualifier("gamesService") RestClient client) {
        this.client = client;
    }

    public GameMetadataResponse fetchMetadata(GameId id) {
        try {
            GameMetadataResponse response = client.get()
                    .uri("/" + id.value())
                    .retrieve()
                    .body(GameMetadataResponse.class);

            if (response == null)
                throw new GameNotFoundException(id);

            return response;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new GameNotFoundException(id);
            throw e;
        } catch (RestClientException e) {
            throw ServiceUnavailableException.GameServiceUnavailable();
        }
    }
}