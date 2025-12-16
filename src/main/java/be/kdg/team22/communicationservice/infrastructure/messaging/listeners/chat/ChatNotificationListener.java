package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.chat;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.chat.DirectMessageReceivedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChatNotificationListener {
    private final NotificationService notifications;

    public ChatNotificationListener(final NotificationService notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = RabbitMQTopology.Q_DIRECT_MESSAGE)
    public void handle(final DirectMessageReceivedEvent event) {
        PlayerId recipient = PlayerId.from(event.recipientId());

        notifications.create(
                recipient,
                NotificationOrigin.DIRECT_MESSAGE,
                "New private message from " + event.senderName(),
                event.messagePreview()
        );
    }
}