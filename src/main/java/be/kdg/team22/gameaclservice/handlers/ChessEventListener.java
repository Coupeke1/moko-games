package be.kdg.team22.gameaclservice.handlers;

import be.kdg.team22.gameaclservice.application.chess.GameTranslationService;
import be.kdg.team22.gameaclservice.config.RabbitMQTopology;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessGameEndedEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessMessageType;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChessEventListener {
    private final GameTranslationService translator;

    public ChessEventListener(GameTranslationService translator) {
        this.translator = translator;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS)
    public void onChessEvent(ChessRegisterEvent message) {
        if (message.messageType() != ChessMessageType.GAME_REGISTERED) return;
        translator.translateAndRegisterGame(message);
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS)
    public void onChessEvent(ChessAchievementEvent message) {
        if (message.messageType() != ChessMessageType.ACHIEVEMENT_ACQUIRED) return;
        translator.translateAndSendAchievement(message);
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS)
    public void onChessEvent(ChessGameEndedEvent message) {
        if (message.messageType() != ChessMessageType.GAME_ENDED) return;
        translator.translateAndSendGameEnded(message);
    }
}
