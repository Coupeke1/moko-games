package be.kdg.team22.gameaclservice.infrastructure.user;

import be.kdg.team22.gameaclservice.domain.exceptions.UserServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExternalUserRepository {
    private final RestClient client;

    public ExternalUserRepository(@Qualifier("userService") final RestClient userServiceRestClient) {
        this.client = userServiceRestClient;
    }

    public UserResponse getUser(final UUID id) {
        try {
            return client.get()
                    .uri("/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(UserResponse.class);
        } catch (RestClientException e) {
            throw new UserServiceNotReachableException(e.toString());
        }
    }
}