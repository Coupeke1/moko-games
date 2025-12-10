package be.kdg.team22.gamesservice.infrastructure.store;

import be.kdg.team22.gamesservice.domain.game.exceptions.StoreServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalStoreRepository {
    private final RestClient restClient;
    public ExternalStoreRepository(@Qualifier("storeRepository") final RestClient restClient) {
        this.restClient = restClient;
    }

    public boolean addToStore(final NewStoreEntryModel request) {
        try {
            ResponseEntity<Void> response = restClient
                    .post()
                    .uri("/")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            throw new StoreServiceNotReachableException(restClient.toString());
        }
    }
}
