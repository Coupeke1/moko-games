package be.kdg.team22.tictactoeservice.infrastructure.ai;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class ExternalAiRepository {
    private final RestClient client;

    public ExternalAiRepository(@Qualifier("aiService") final RestClient client) {
        this.client = client;
    }

    public AiMoveResponse requestMove(AiMoveRequest request) {
        try {
            final AiMoveResponse response = client.post()
                    .uri("/move")
                    .body(request)
                    .retrieve()
                    .body(AiMoveResponse.class);
            return response;
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw exception;
        }
    }
}