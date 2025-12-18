package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.session;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.session.LobbyInviteEvent;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.session.PlayerJoinedLobbyEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SessionNotificationListener {
    private final NotificationService notifications;

    public SessionNotificationListener(final NotificationService notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_LOBBY_INVITE)
    public void handle(final LobbyInviteEvent event) {
        PlayerId recipient = PlayerId.from(event.targetUserId());

        notifications.create(recipient, NotificationOrigin.LOBBY_INVITE, "Invited to lobby", String.format("\"%s\" has invited you to a lobby for \"%s\"!", event.inviterName(), event.gameName()));
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_PLAYER_JOINED)
    public void handle(final PlayerJoinedLobbyEvent event) {
        PlayerId host = PlayerId.from(event.hostUserId());

        notifications.create(host, NotificationOrigin.PLAYER_JOINED_LOBBY, "Player joined", String.format("\"%s\" has joined your lobby!", event.playerName()));
    }
}