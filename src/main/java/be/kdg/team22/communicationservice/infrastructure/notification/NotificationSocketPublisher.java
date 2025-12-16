package be.kdg.team22.communicationservice.infrastructure.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;
import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationSocketPublisher {
    private final SimpMessagingTemplate template;

    public NotificationSocketPublisher(final SimpMessagingTemplate template) {
        this.template = template;
    }

    public void publishToPlayer(final PlayerId playerId, final Notification notification) {
        String userId = playerId.value().toString();
        NotificationModel model = NotificationModel.from(notification);
        template.convertAndSendToUser(userId, "/queue/notifications", model);
    }
}