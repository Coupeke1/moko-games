package be.kdg.team22.storeservice.infrastructure.user;

import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalUserRepository {

    private final RestClient client;

    public ExternalUserRepository(@Qualifier("libraryService") final RestClient client) {
        this.client = client;
    }

    public boolean userOwnsGame(final GameId gameId, final String jwtToken) {
        try {
            Boolean ownsGame = client.get().uri("/" + gameId.value()).header("Authorization", "Bearer " + jwtToken).retrieve().body(Boolean.class);
            return ownsGame != null && ownsGame;
        } catch (RestClientException e) {
            throw ServiceUnavailableException.UserServiceUnavailable();
        }
    }

}