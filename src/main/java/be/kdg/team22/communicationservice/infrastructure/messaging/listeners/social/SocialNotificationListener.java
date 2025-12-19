package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.social;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.social.FriendRequestAcceptedEvent;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.social.FriendRequestReceivedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SocialNotificationListener {
    private final NotificationService notifications;

    public SocialNotificationListener(final NotificationService notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_FRIEND_REQUEST_RECEIVED)
    public void handle(final FriendRequestReceivedEvent event) {
        PlayerId recipient = PlayerId.from(event.targetUserId());

        notifications.create(recipient, NotificationOrigin.FRIEND_REQUEST_RECEIVED, "New friend request", String.format("\"%s\" has sent you a friend request!", event.senderName()));
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_FRIEND_REQUEST_ACCEPTED)
    public void handle(final FriendRequestAcceptedEvent event) {
        PlayerId recipient = PlayerId.from(event.targetUserId());

        notifications.create(recipient, NotificationOrigin.FRIEND_REQUEST_ACCEPTED, "Friend request accepted", String.format("\"%s\" has accepted your friend request!", event.senderName()));
    }
}