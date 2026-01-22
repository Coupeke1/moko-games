package be.kdg.team22.tictactoeservice.handlers;

import be.kdg.team22.tictactoeservice.application.GameService;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.Move;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.events.BotMoveRequestedEvent;
import be.kdg.team22.tictactoeservice.infrastructure.bot.BotMoveRequest;
import be.kdg.team22.tictactoeservice.infrastructure.bot.BotMoveResponse;
import be.kdg.team22.tictactoeservice.infrastructure.bot.ExternalBotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BotMoveEventListener {
    private static final Logger logger = LoggerFactory.getLogger(BotMoveEventListener.class);
    private final GameService gameService;
    private final ExternalBotRepository botRepository;

    public BotMoveEventListener(GameService gameService, ExternalBotRepository botRepository) {
        this.gameService = gameService;
        this.botRepository = botRepository;
    }

    @Async
    @EventListener
    public void handleBotMoveRequest(BotMoveRequestedEvent event) {
        BotMoveRequest request = new BotMoveRequest(event.gameId(), event.gameName(),
                event.board(), event.currentPlayer().role().name(), event.botPlayer().name()
        );
        BotMoveResponse response = botRepository.requestMove(request);

        if (!event.expectResponse()) return;

        if (response != null) {
            GameId gameId = new GameId(UUID.fromString(event.gameId()));
            PlayerId botPlayerId = event.currentPlayer().id();

            Move move = new Move(botPlayerId, response.row(), response.col());
            gameService.requestMove(gameId, botPlayerId, move);
            logger.info("Bot move executed for {} game {}", event.gameName(), event.gameId());
        }
    }
}