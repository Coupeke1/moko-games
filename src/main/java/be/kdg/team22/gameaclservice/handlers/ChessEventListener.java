package be.kdg.team22.gameaclservice.handlers;

import be.kdg.team22.gameaclservice.application.chess.GameTranslationService;
import be.kdg.team22.gameaclservice.config.RabbitMQTopology;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessGameEndedEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChessEventListener {
    private final GameTranslationService translator;

    public ChessEventListener(GameTranslationService translator) {
        this.translator = translator;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS_REGISTERED)
    public void onGameRegistered(ChessRegisterEvent message) {
        translator.translateAndRegisterGame(message);
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS_ACHIEVEMENT)
    public void onAchievementAcquired(ChessAchievementEvent message) {
        translator.translateAndSendAchievement(message);
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS_ENDED)
    public void onGameEnded(ChessGameEndedEvent message) {
        translator.translateAndSendGameEnded(message);
    }
}
