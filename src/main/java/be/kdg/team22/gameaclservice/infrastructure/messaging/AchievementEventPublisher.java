package be.kdg.team22.gameaclservice.infrastructure.messaging;

import be.kdg.team22.gameaclservice.config.RabbitMQTopology;
import be.kdg.team22.gameaclservice.events.outbound.GameAchievementEvent;
import be.kdg.team22.gameaclservice.events.outbound.GameEndedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AchievementEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public AchievementEventPublisher(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAchievementEvent(GameAchievementEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_PLATFORM_GAMEPLAY,
                RabbitMQTopology.ROUTING_KEY_PLATFORM_ACHIEVEMENT_AQUIRED,
                event
        );
    }

    public void publishGameEndedEvent(GameEndedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_PLATFORM_GAMEPLAY,
                RabbitMQTopology.ROUTING_KEY_PLATFORM_GAME_ENDED,
                event
        );
    }
}