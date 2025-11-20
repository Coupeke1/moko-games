package be.kdg.team22.socialservice.friends.infrastructure.http;

import be.kdg.team22.socialservice.friends.domain.Username;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080/api/profiles")
                .build();
    }

    public record UserResponse(UUID id, String username) { }

    public Optional<UserResponse> getByUsername(Username username) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/find/{username}", username)
                    .retrieve()
                    .body(UserResponse.class));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw e;
        } catch (RestClientException e) {
            throw new IllegalStateException("User-service unreachable", e);
        }
    }

    public UserResponse getById(UUID id) {
        try {
            return restClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .body(UserResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("User with id '%s' not found".formatted(id));
            }
            throw e;
        } catch (RestClientException e) {
            throw new IllegalStateException("User-service unreachable", e);
        }
    }
}
