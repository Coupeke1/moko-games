package be.kdg.team22.sessionservice.infrastructure.player;

import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.exceptions.NotReachableException;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

@Component
public class ExternalPlayersRepository {
    private final RestClient client;

    public ExternalPlayersRepository(@Qualifier("userService") final RestClient client) {
        this.client = client;
    }

    public Optional<PlayerResponse> getById(final UUID id, final String token) {
        try {
            return Optional.ofNullable(
                    client.get()
                            .uri("/{id}", id)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .body(PlayerResponse.class));
        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() != HttpStatus.NOT_FOUND)
                throw exception;

            throw new PlayerNotFoundException(PlayerId.from(id));
        } catch (RestClientException exception) {
            throw new NotReachableException();
        }
    }

    public Optional<BotProfileResponse> createBot() {
        try {
            return Optional.ofNullable(
                    client.post()
                            .uri("/bot")
                            .retrieve()
                            .body(BotProfileResponse.class));
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw ex;
            }
            throw ex;
        } catch (RestClientException ex) {
            throw new NotReachableException();
        }
    }
}