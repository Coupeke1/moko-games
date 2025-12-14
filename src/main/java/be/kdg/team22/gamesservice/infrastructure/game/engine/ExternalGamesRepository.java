package be.kdg.team22.gamesservice.infrastructure.game.engine;

import be.kdg.team22.gamesservice.api.game.models.CheckersSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.GameSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.TicTacToeSettingsModel;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.exceptions.EngineGameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.EngineNotReachableException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.UUID;

@Component
public class ExternalGamesRepository {

    public UUID startExternalGame(Game game, StartGameRequest request, final Jwt token) {

        String engineUrl = game.baseUrl() + game.startEndpoint();

        EngineCreateGameRequest engineReq = new EngineCreateGameRequest(
                request.players(),
                mapSettings(request.settings()),
                request.aiPlayer()
        );

        RestClient client = createRestClient(engineUrl);

        try {
            EngineGameResponse response = client.post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("aiPlayer", request.aiPlayer())
                            .build()
                    )
                    .header("Authorization", "Bearer " + token.getTokenValue())
                    .body(engineReq)
                    .retrieve()
                    .body(EngineGameResponse.class);

            return Objects.requireNonNull(response).id();

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EngineGameNotFoundException(request.gameId());
            }
            throw ex;

        } catch (RestClientException ex) {
            throw new EngineNotReachableException(engineUrl);
        }
    }

    RestClient createRestClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    private Object mapSettings(GameSettingsModel model) {
        return switch (model) {
            case TicTacToeSettingsModel t -> new EngineTicTacToeSettings(t.boardSize());
            case CheckersSettingsModel c -> new EngineCheckersSettings(c.boardSize(), c.flyingKings());
        };
    }
}