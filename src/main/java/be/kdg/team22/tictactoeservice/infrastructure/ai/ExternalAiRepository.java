package be.kdg.team22.tictactoeservice.infrastructure.ai;

import be.kdg.team22.tictactoeservice.domain.game.exceptions.AiMoveRequestFailedException;
import be.kdg.team22.tictactoeservice.domain.game.exceptions.AiServiceNotReachableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalAiRepository {
    private final RestClient client;

    public ExternalAiRepository(@Qualifier("aiService") final RestClient client) {
        this.client = client;
    }

    public AiMoveResponse requestMove(AiMoveRequest request) {
        try {
            return client.post()
                    .uri("/ai-player/move")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(AiMoveResponse.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new AiMoveRequestFailedException(request.toString());
            }

            throw exception;
        } catch (RestClientException ex) {
            throw new AiServiceNotReachableException(client.toString());
        }
    }
}