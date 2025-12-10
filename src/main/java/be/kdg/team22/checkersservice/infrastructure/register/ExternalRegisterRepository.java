package be.kdg.team22.checkersservice.infrastructure.register;

import be.kdg.team22.checkersservice.domain.register.GameRegisterId;
import be.kdg.team22.checkersservice.domain.register.exceptions.GameServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalRegisterRepository {
    private final RestClient client;

    public ExternalRegisterRepository(@Qualifier("registerRepository") final RestClient client) {
        this.client = client;
    }

    public GameResponse getGame(String name) {
        try {
            return client.get()
                    .uri("/{name}", name)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(GameResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (RestClientException exception) {
            throw new GameServiceNotReachableException(client.toString());
        }
    }

    public GameResponse updateGame(GameRegisterId id, RegisterGameRequest request) {
        try {
            ResponseEntity<GameResponse> response = client.put()
                    .uri("/{id}", id.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(GameResponse.class);
            return response.getBody();
        } catch (RestClientException exception) {
            throw new GameServiceNotReachableException(client.toString());
        }
    }

    public boolean registerGame(RegisterGameRequest request) {
        try {
            ResponseEntity<Void> response = client.post()
                    .uri("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toBodilessEntity();
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException exception) {
            throw new GameServiceNotReachableException(client.toString());
        }
    }
}