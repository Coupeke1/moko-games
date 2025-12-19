package be.kdg.team22.gameaclservice.handlers;

import be.kdg.team22.gameaclservice.application.TranslationService;
import be.kdg.team22.gameaclservice.config.RabbitMQTopology;
import be.kdg.team22.gameaclservice.events.inbound.ChessAchievementEvent;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChessEventListener {
    private final TranslationService translator;

    public ChessEventListener(TranslationService translator) {
        this.translator = translator;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS)
    public void onChessEvent(ChessRegisterEvent message) {
        translator.translateAndRegisterGame(message);
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_CHESS_EVENTS)
    public void onChessEvent(ChessAchievementEvent message) {
        translator.translateAndSendAchievement(message);
    }
}
