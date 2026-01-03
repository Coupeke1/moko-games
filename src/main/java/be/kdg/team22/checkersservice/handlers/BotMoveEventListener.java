package be.kdg.team22.checkersservice.handlers;

import be.kdg.team22.checkersservice.application.GameService;
import be.kdg.team22.checkersservice.domain.game.GameId;
import be.kdg.team22.checkersservice.domain.move.Move;
import be.kdg.team22.checkersservice.domain.player.PlayerId;
import be.kdg.team22.checkersservice.events.BotMoveRequestedEvent;
import be.kdg.team22.checkersservice.infrastructure.bot.BotMoveRequest;
import be.kdg.team22.checkersservice.infrastructure.bot.BotMoveResponse;
import be.kdg.team22.checkersservice.infrastructure.bot.ExternalBotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class BotMoveEventListener {
    private static final Logger logger = LoggerFactory.getLogger(BotMoveEventListener.class);
    private final GameService gameService;
    private final ExternalBotRepository aiRepository;

    public BotMoveEventListener(GameService gameService, ExternalBotRepository aiRepository) {
        this.gameService = gameService;
        this.aiRepository = aiRepository;
    }

    @Async
    @EventListener
    public void handleBotMoveRequest(BotMoveRequestedEvent event) {
        BotMoveRequest request = new BotMoveRequest(event.gameId(), event.gameName(),
                event.board(), String.valueOf(event.currentPlayer().symbol()), String.valueOf(event.botPlayer().symbol()), event.kingMovementMode().name()
        );
        BotMoveResponse response = aiRepository.requestMove(request);

        if (!event.expectResponse()) return;

        if (response != null) {
            GameId gameId = new GameId(UUID.fromString(event.gameId()));
            PlayerId botPlayerId = gameService.getById(gameId).players().stream()
                    .filter(p -> p.role() == event.botPlayer())
                    .findFirst()
                    .orElseThrow()
                    .id();
            List<Integer> cells = Arrays.stream(response.executedMoves().split("-"))
                    .map(Integer::parseInt)
                    .toList();

            Move move = new Move(botPlayerId, cells);
            gameService.requestMove(gameId, botPlayerId, move);
            logger.info("Bot move executed for {} game {}", event.gameName(), event.gameId());
        }
    }
}