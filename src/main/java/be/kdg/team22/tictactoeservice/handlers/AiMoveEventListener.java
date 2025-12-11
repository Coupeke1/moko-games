package be.kdg.team22.tictactoeservice.handlers;

import be.kdg.team22.tictactoeservice.application.GameService;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.events.AiMoveRequestedEvent;
import be.kdg.team22.tictactoeservice.infrastructure.ai.AiMoveRequest;
import be.kdg.team22.tictactoeservice.infrastructure.ai.AiMoveResponse;
import be.kdg.team22.tictactoeservice.infrastructure.ai.ExternalAiRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AiMoveEventListener {
    private final GameService gameService;
    private final ExternalAiRepository aiRepository;

    public AiMoveEventListener(GameService gameService, ExternalAiRepository aiRepository) {
        this.gameService = gameService;
        this.aiRepository = aiRepository;
    }

    @Async
    @EventListener
    public void handleAiMoveRequest(AiMoveRequestedEvent event) {
        AiMoveRequest request = new AiMoveRequest(event.gameId(), event.gameName(),
                event.board(), event.currentPlayer().role().name(), event.aiPlayer().name()
        );
        AiMoveResponse response = aiRepository.requestMove(request);

        if (!event.expectResponse()) return;

        if (response != null) {
            GameId gameId = new GameId(UUID.fromString(event.gameId()));
            PlayerId aiPlayerId = event.currentPlayer().id();

            Move move = new Move(aiPlayerId, response.row(), response.col());
            gameService.requestMove(gameId, aiPlayerId, move);
        }
    }
}
