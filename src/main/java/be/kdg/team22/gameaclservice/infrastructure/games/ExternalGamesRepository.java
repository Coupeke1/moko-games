package be.kdg.team22.gameaclservice.infrastructure.games;

import be.kdg.team22.gameaclservice.domain.exceptions.GameServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalGamesRepository {
    private final RestClient client;

    public ExternalGamesRepository(@Qualifier("gameService") final RestClient gameServiceRestClient) {
        this.client = gameServiceRestClient;
    }

    public boolean registerGame(RegisterGameRequest request) {
        try {
            ResponseEntity<Void> response = client.put()
                    .uri("/{name}", request.name())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            throw new GameServiceNotReachableException(client.toString());
        }
    }
}