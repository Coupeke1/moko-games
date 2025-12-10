package be.kdg.team22.checkersservice.handlers;

import be.kdg.team22.checkersservice.application.GameService;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.events.AiMoveRequestedEvent;
import be.kdg.team22.checkersservice.infrastructure.ai.AiMoveRequest;
import be.kdg.team22.checkersservice.infrastructure.ai.AiMoveResponse;
import be.kdg.team22.checkersservice.infrastructure.ai.ExternalAiRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
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
                event.board(), String.valueOf(event.currentPlayer().symbol()), String.valueOf(event.aiPlayer().symbol()), event.kingMovementMode().name()
        );
        AiMoveResponse response = aiRepository.requestMove(request);

        if (!event.expectResponse()) return;

        if (response != null) {
            GameId gameId = new GameId(UUID.fromString(event.gameId()));
            PlayerId aiPlayerId = gameService.getById(gameId).players().stream()
                    .filter(p -> p.role() == event.aiPlayer())
                    .findFirst()
                    .orElseThrow()
                    .id();
            List<Integer> cells = Arrays.stream(response.executedMoves().split("-"))
                    .map(Integer::parseInt)
                    .toList();

            Move move = new Move(aiPlayerId, cells);
            gameService.requestMove(gameId, aiPlayerId, move);
        }
    }
}
