package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.achievements;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.achievements.AchievementUnlockedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AchievementNotificationListener {
    private final NotificationService notifications;

    public AchievementNotificationListener(final NotificationService notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = RabbitMQTopology.Q_ACHIEVEMENT_UNLOCKED)
    public void handle(final AchievementUnlockedEvent event) {
        PlayerId recipient = PlayerId.from(event.playerId());

        notifications.create(recipient, NotificationOrigin.ACHIEVEMENT_UNLOCKED, "Achievement unlocked", event.achievementName());
    }
}