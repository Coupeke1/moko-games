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

    @RabbitListener(queues = RabbitMQTopology.Q_LOBBY_INVITE)
    public void handle(final LobbyInviteEvent event) {
        PlayerId recipient = PlayerId.from(event.targetUserId());

        notifications.create(
                recipient,
                NotificationOrigin.LOBBY_INVITE,
                "Invited for lobby",
                event.inviterName() + " has invited you for " + event.gameName() +
                        " (lobby " + event.lobbyId() + ")."
        );
    }

    @RabbitListener(queues = RabbitMQTopology.Q_PLAYER_JOINED)
    public void handle(final PlayerJoinedLobbyEvent event) {
        PlayerId host = PlayerId.from(event.hostUserId());

        notifications.create(
                host,
                NotificationOrigin.PLAYER_JOINED_LOBBY,
                "New player joined",
                event.playerName() + " has joined " + event.lobbyId() + "."
        );
    }
}