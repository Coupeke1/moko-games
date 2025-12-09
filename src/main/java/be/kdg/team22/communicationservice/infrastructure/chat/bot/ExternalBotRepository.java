package be.kdg.team22.communicationservice.infrastructure.chat.bot;

import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.exceptions.BotServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Repository
public class ExternalBotRepository implements BotChatRepository {

    private final RestClient client;
    private final int teamNumber;

    public ExternalBotRepository(
                        @Qualifier("botService") final RestClient client,
            @Value("${business.bot-service.team-number}") final int teamNumber
    ) {
        this.client = client;
        this.teamNumber = teamNumber;
    }

    @Override
        public BotResponse askBot(final String question, final String gameName) {
        BotChatRequest request = new BotChatRequest(question, teamNumber, gameName);

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
}