package be.kdg.team22.communicationservice.infrastructure.chat.bot;

import be.kdg.team22.communicationservice.config.BotServiceProperties;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.exceptions.BotServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Repository
public class ExternalBotRepository implements BotChatRepository {

    private final RestClient client;
    private final BotServiceProperties props;

    public ExternalBotRepository(
            @Qualifier("botService") final RestClient client, BotServiceProperties props

    ) {
        this.client = client;
        this.props = props;
    }

    @Override
    public BotResponse askBot(final String question, final String gameName) {
        BotChatRequest request = new BotChatRequest(question, props.teamNumber(), gameName);

        try {
            BotResponse response = client.post()
                    .uri("/chat")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw BotServiceException.badResponse("Client error: " + res.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                        throw BotServiceException.unavailable();
                    })
                    .body(BotResponse.class);

            if (response == null || response.answer() == null) {
                throw BotServiceException.badResponse("Empty response from bot service");
            }

            return response;

        } catch (RestClientException e) {
            throw BotServiceException.requestFailed(e);
        }
    }

    public boolean uploadPdf(byte[] pdfBytes, String filename) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("teamNumber", String.valueOf(props.teamNumber()));

        body.add("file", new ByteArrayResource(pdfBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        try {
            ResponseEntity<Void> response = client.post()
                    .uri("/upload-document")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            throw BotServiceException.requestFailed(ex);
        }
    }
}