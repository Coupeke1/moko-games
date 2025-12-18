package be.kdg.team22.checkersservice.infrastructure.bot;

import be.kdg.team22.checkersservice.domain.game.exceptions.BotMoveRequestFailedException;
import be.kdg.team22.checkersservice.domain.game.exceptions.BotServiceNotReachableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ExternalBotRepository {
    private final Logger logger;
    private final RestClient primaryClient;
    private final RestClient fallbackClient;

    public ExternalBotRepository(
            @Qualifier("botService") final RestClient primaryClient,
            @Qualifier("botServiceFallback") final RestClient fallbackClient
    ) {
        logger = LoggerFactory.getLogger(ExternalBotRepository.class);
        this.primaryClient = primaryClient;
        this.fallbackClient = fallbackClient;
    }

    public BotMoveResponse requestMove(BotMoveRequest request) {
        BotMoveResponse response = null;
        try {
            response = tryRequestWithClient(primaryClient, "Primary", request);
        } catch (BotMoveRequestFailedException | BotServiceNotReachableException ex) {
            logger.error(ex.getMessage());
            try {
                response = tryRequestWithClient(fallbackClient, "Fallback", request);
            } catch (BotMoveRequestFailedException | BotServiceNotReachableException fallbackEx) {
                logger.error(fallbackEx.getMessage());
            }
        }
        return response;
    }

    private BotMoveResponse tryRequestWithClient(RestClient client, String clientName, BotMoveRequest request) {
        try {
            return client.post()
                    .uri("/ai-player/move")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(BotMoveResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new BotMoveRequestFailedException(clientName, ex.getMessage());
        } catch (RestClientException ex) {
            throw new BotServiceNotReachableException(clientName);
        }
    }
}