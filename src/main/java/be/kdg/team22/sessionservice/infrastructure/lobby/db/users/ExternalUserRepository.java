package be.kdg.team22.sessionservice.infrastructure.lobby.db.users;

import be.kdg.team22.sessionservice.domain.lobby.exceptions.ServiceNotReachableException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExternalUserRepository {

    private final RestClient client;

    public ExternalUserRepository(@Qualifier("userService") RestClient client) {
        this.client = client;
    }

    public UserResponse getById(UUID id) {
        try {
            UserResponse response = client.get()
                    .uri("/api/profiles/{id}", id)
                    .retrieve()
                    .body(UserResponse.class);

            if (response == null)
                throw new UserNotFoundException(id);

            return response;

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new UserNotFoundException(id);

            throw ex;

        } catch (RestClientException ex) {
            throw ServiceNotReachableException.userServiceNotReachable();
        }
    }
}
