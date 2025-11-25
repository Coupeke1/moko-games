package be.kdg.team22.gamesservice.infrastructure.game.engine;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.exceptions.EngineGameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.EngineNotReachableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.util.UUID;

@Component
public class ExternalGamesRepository {
    public UUID startExternalGame(Game game, EngineStartRequest request) {
        String targetUrl = game.baseUrl() + game.startEndpoint();

        RestClient client = RestClient.builder()
                .baseUrl(targetUrl)
                .build();

        try {
            EngineStartResponse response = client.post()
                    .uri("")
                    .body(request)
                    .retrieve()
                    .body(EngineStartResponse.class);

            return response.gameInstanceId();

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EngineGameNotFoundException(request.templateId());
            }

            throw ex;

        } catch (RestClientException ex) {
            throw new EngineNotReachableException(targetUrl);
        }
    }
}