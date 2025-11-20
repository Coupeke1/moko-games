package be.kdg.team22.socialservice.infrastructure.friends.user;

import be.kdg.team22.socialservice.domain.friends.user.Username;
import be.kdg.team22.socialservice.domain.friends.user.exceptions.NotFoundException;
import be.kdg.team22.socialservice.domain.friends.user.exceptions.NotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

@Component
public class ExternalUserRepository {
    private final RestClient client;

    public ExternalUserRepository(@Qualifier("userService") final RestClient client) {
        this.client = client;
    }

    public Optional<UserResponse> getByUsername(Username username) {
        try {
            UserResponse response = client.get().uri("/find/{username}", username).retrieve().body(UserResponse.class);
            return Optional.ofNullable(response);
        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND)
                return Optional.empty();
            throw exception;
        } catch (
                RestClientException exception) {
            throw new NotReachableException("user-service");
        }
    }

    public UserResponse getById(UUID id) {
        try {
            return client.get().uri("/{id}", id).retrieve().body(UserResponse.class);
        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() != HttpStatus.NOT_FOUND)
                throw exception;

            throw new NotFoundException(id);
        } catch (
                RestClientException exception) {
            throw new NotReachableException("user-service");
        }
    }
}