package be.kdg.team22.gameaclservice.config;

import be.kdg.team22.gameaclservice.domain.game.AchievementAcquiredMessage;
import be.kdg.team22.gameaclservice.domain.game.GameEndedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class ChessEventListener {
    private final RabbitTemplate platformRabbitTemplate;

    public ChessEventListener(RabbitTemplate rabbitTemplate) {
        this.platformRabbitTemplate = rabbitTemplate;
    }

    public void handleMessage(Object message) {
        if (message instanceof AchievementAcquiredMessage achievement) {
            forwardAchievementToPlatform(achievement);
        } else if (message instanceof GameEndedMessage gameEnded) {
            forwardGameEndedToPlatform(gameEnded);
        }
    }

    private void forwardAchievementToPlatform(AchievementAcquiredMessage achievement) {
        platformRabbitTemplate.convertAndSend(
                RabbitMQTopology.PLATFORM_EXCHANGE,
                RabbitMQTopology.ACHIEVEMENT_ROUTING_KEY,
                achievement
        );
    }

    private void forwardGameEndedToPlatform(GameEndedMessage gameEnded) {
        platformRabbitTemplate.convertAndSend(
                RabbitMQTopology.PLATFORM_EXCHANGE,
                RabbitMQTopology.GAME_ENDED_ROUTING_KEY,
                gameEnded
        );
    }
}
