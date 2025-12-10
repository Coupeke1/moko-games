package be.kdg.team22.gamesservice.infrastructure.store;

import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.exceptions.StoreServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalStoreRepository {
    private final RestClient restClient;

    public ExternalStoreRepository(@Qualifier("storeRepository") final RestClient restClient) {
        this.restClient = restClient;
    }

    public void createStoreEntry(final NewStoreEntryModel request) {
        try {
            restClient
                    .post()
                    .uri("/")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new StoreServiceNotReachableException(restClient.toString());
        }
    }

    public StoreEntryModel getStoreEntry(final GameId id) {
        try {
            return restClient
                    .get()
                    .uri("/{id}", id.value())
                    .retrieve()
                    .body(StoreEntryModel.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (RestClientException e) {
            throw new StoreServiceNotReachableException(restClient.toString());
        }
    }

    public void updateStoreEntry(final GameId id, final UpdateStoreEntryModel request) {
        try {
            restClient
                    .put()
                    .uri("/{id}", id.value())
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new StoreServiceNotReachableException(restClient.toString());
        }
    }
}
