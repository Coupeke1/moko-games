package be.kdg.team22.tictactoeservice.domain.exceptions;

import org.springframework.web.client.RestClientException;

public class BotServiceException extends RuntimeException {
    public BotServiceException(String message) {
        super(message);
    }

    public static BotServiceException requestFailed(RestClientException exceptionMessage) {
        return new BotServiceException(exceptionMessage.getMessage());
    }
}
