package be.kdg.team22.userservice.infrastructure.messaging;

import be.kdg.team22.userservice.config.RabbitMQTopology;
import be.kdg.team22.userservice.events.AchievementUnlockedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AchievementEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public AchievementEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAchievementUnlocked(AchievementUnlockedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_ACHIEVEMENTS,
                "achievement.unlocked",
                event
        );
    }
}