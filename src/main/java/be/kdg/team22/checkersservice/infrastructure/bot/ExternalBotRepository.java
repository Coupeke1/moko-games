package be.kdg.team22.checkersservice.infrastructure.bot;

import be.kdg.team22.checkersservice.domain.game.exceptions.BotMoveRequestFailedException;
import be.kdg.team22.checkersservice.domain.game.exceptions.BotServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalBotRepository {
    private final RestClient client;

    public ExternalBotRepository(@Qualifier("botService") final RestClient client) {
        this.client = client;
    }

    public BotMoveResponse requestMove(BotMoveRequest request) {
        try {
            return client.post()
                    .uri("/ai-player/move")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(BotMoveResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BotMoveRequestFailedException(request.toString());
            }

            throw exception;
        } catch (RestClientException ex) {
            throw new BotServiceNotReachableException(client.toString());
        }
    }
}