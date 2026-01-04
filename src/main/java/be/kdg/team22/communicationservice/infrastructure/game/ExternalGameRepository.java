package be.kdg.team22.communicationservice.infrastructure.game;

import be.kdg.team22.communicationservice.domain.chat.channel.GameId;
import be.kdg.team22.communicationservice.domain.chat.exceptions.GameNotFoundException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ExternalGameRepository {
    private final RestClient client;

    public ExternalGameRepository(@Qualifier("gamesService") final RestClient client) {
        this.client = client;
    }

    public GameResponse getGame(final UUID id) {
        try {
            GameResponse response = client.get().uri("/api/games/{id}", id).retrieve().body(GameResponse.class);

            if (response == null)
                throw new GameNotFoundException(GameId.from(id));

            return response;

        } catch (
                HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new GameNotFoundException(GameId.from(id));

            throw exception;
        } catch (RestClientException exception) {
            System.out.println("    " + exception);
            throw ServiceUnavailableException.gamesServiceUnavailable();
        }
    }
}