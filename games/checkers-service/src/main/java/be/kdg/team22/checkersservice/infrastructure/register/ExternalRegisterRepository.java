package be.kdg.team22.checkersservice.infrastructure.register;

import be.kdg.team22.checkersservice.domain.register.exceptions.GameServiceNotReachableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalRegisterRepository {
    private final RestClient client;

    private final Logger logger = LoggerFactory.getLogger(ExternalRegisterRepository.class);

    public ExternalRegisterRepository(@Qualifier("registerRepository") final RestClient client) {
        this.client = client;
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
        } catch (RestClientException exception) {
            logger.error("Error registering game", exception);
            throw new GameServiceNotReachableException(client.toString());
        }
    }
}