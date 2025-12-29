package be.kdg.team22.communicationservice.infrastructure.chat.bot;

import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.exceptions.BotServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Repository
public class ExternalBotRepository implements BotChatRepository {

    public static final int TEAM_NUMBER = 22;

    private final Logger logger = LoggerFactory.getLogger(ExternalBotRepository.class);
    private final RestClient primaryClient;
    private final RestClient fallbackClient;

    public ExternalBotRepository(
            @Qualifier("botService") final RestClient primaryClient,
            @Qualifier("botServiceFallback") final RestClient fallbackClient
    ) {
        this.primaryClient = primaryClient;
        this.fallbackClient = fallbackClient;
    }

    @Override
    public BotResponse askBot(final String question, final String gameName) {
        BotChatRequest request = new BotChatRequest(question, TEAM_NUMBER, gameName);

        try {
            return tryAskWithClient(primaryClient, "Primary", request);
        } catch (ResourceAccessException ex) {
            logger.warn("Primary chat service not reachable, trying fallback. {}", ex.getMessage());
            try {
                return tryAskWithClient(fallbackClient, "Fallback", request);
            } catch (RestClientException fallbackEx) {
                throw BotServiceException.requestFailed(fallbackEx);
            }
        } catch (RestClientException ex) {
            // bereikbaar maar 4xx/5xx/etc => geen fallback
            throw BotServiceException.requestFailed(ex);
        }
    }

    private BotResponse tryAskWithClient(RestClient client, String clientName, BotChatRequest request) {
        BotResponse response = client.post()
                .uri("/chat")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw BotServiceException.badResponse(clientName + " client error: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw BotServiceException.unavailable();
                })
                .body(BotResponse.class);

        if (response == null || response.answer() == null) {
            throw BotServiceException.badResponse(clientName + " returned empty response");
        }
        return response;
    }

    public boolean uploadPdf(byte[] pdfBytes, String filename) {
        try {
            return tryUploadWithClient(primaryClient, "Primary", pdfBytes, filename);
        } catch (ResourceAccessException ex) {
            logger.warn("Primary chat service upload not reachable, trying fallback. {}", ex.getMessage());
            try {
                return tryUploadWithClient(fallbackClient, "Fallback", pdfBytes, filename);
            } catch (RestClientException fallbackEx) {
                throw BotServiceException.requestFailed(fallbackEx);
            }
        } catch (RestClientException ex) {
            throw BotServiceException.requestFailed(ex);
        }
    }

    private boolean tryUploadWithClient(RestClient client, String clientName, byte[] pdfBytes, String filename) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(pdfBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        ResponseEntity<Void> response = client.post()
                .uri("/upload-document")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toBodilessEntity();

        logger.debug("{} upload responded with status: {}", clientName, response.getStatusCode());
        return response.getStatusCode().is2xxSuccessful();
    }
}