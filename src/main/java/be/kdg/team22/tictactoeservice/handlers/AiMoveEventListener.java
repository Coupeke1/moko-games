package be.kdg.team22.tictactoeservice.handlers;

import be.kdg.team22.tictactoeservice.api.models.AiMoveResponse;
import be.kdg.team22.tictactoeservice.application.GameService;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.events.AiMoveRequestedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class AiMoveEventListener {
    @Value("${ai.url.move}")
    String url;

    private final GameService gameService;
    private final RestTemplate restTemplate = new RestTemplate();

    public AiMoveEventListener(GameService gameService) {
        this.gameService = gameService;
    }

    @Async
    @EventListener
    public void handleAiMoveRequest(AiMoveRequestedEvent event) {
        AiMoveResponse response = restTemplate.postForObject(url, event, AiMoveResponse.class);

        System.out.println(response);
        if (response != null) {
            GameId gameId = new GameId(UUID.fromString(event.gameId()));
            PlayerId aiPlayerId = gameService.getGame(gameId).players().stream()
                    .filter(p -> p.role() == event.aiPlayer())
                    .findFirst()
                    .orElseThrow()
                    .id();

            Move move = new Move(gameId, aiPlayerId, response.row(), response.col());
            gameService.requestMove(gameId, move);
        }
    }
}
