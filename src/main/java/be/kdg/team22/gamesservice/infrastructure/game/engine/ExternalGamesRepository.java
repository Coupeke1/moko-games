package be.kdg.team22.gamesservice.infrastructure.game.engine;

import be.kdg.team22.gamesservice.api.game.models.CheckersSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.GameSettingsModel;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.TicTacToeSettingsModel;
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

    public UUID startExternalGame(Game game, StartGameRequest request) {

        String engineUrl = game.baseUrl() + game.startEndpoint();

        EngineCreateGameRequest engineReq = new EngineCreateGameRequest(
                request.players(),
                mapSettings(request.settings())
        );

        RestClient client = RestClient.builder()
                .baseUrl(engineUrl)
                .build();

        try {
            EngineStartResponse response = client.post()
                    .uri("")
                    .body(engineReq)
                    .retrieve()
                    .body(EngineStartResponse.class);

            return response.gameInstanceId();

        } catch (HttpClientErrorException ex) {

            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EngineGameNotFoundException(request.gameId());
            }

            throw ex;

        } catch (RestClientException ex) {
            throw new EngineNotReachableException(engineUrl);
        }
    }

    private Object mapSettings(GameSettingsModel model) {
        return switch (model) {
            case TicTacToeSettingsModel t -> new EngineTicTacToeSettings(t.boardSize());
            case CheckersSettingsModel c -> new EngineCheckersSettings(c.boardSize(), c.flyingKings());
        };
    }
}