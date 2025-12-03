package be.kdg.team22.storeservice.infrastructure.user;

import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.UUID;

@Component
public class ExternalUserRepository {

    private final RestClient client;

    public ExternalUserRepository(@Qualifier("libraryService") final RestClient client) {
        this.client = client;
    }

    public boolean userOwnsGame(UUID gameId, String jwtToken) {
        try {
            List<LibraryGameResponse> library = client.get()
                    .uri("/me")
                    .header("Authorization", "Bearer " + jwtToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<LibraryGameResponse>>() {
                    });

            if (library == null) return false;

            return library.stream().anyMatch(game -> game.id().equals(gameId));

        } catch (RestClientException e) {
            throw ServiceUnavailableException.GameServiceUnavailable();
        }
    }
}