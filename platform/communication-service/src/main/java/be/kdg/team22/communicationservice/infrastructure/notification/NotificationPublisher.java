package be.kdg.team22.communicationservice.infrastructure.notification;

import be.kdg.team22.communicationservice.api.notification.models.NotificationModel;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {
    private final RabbitTemplate template;

    public NotificationPublisher(final RabbitTemplate template) {
        this.template = template;
    }

    public void publishToPlayer(final PlayerId playerId, final Notification notification) {
        NotificationModel model = NotificationModel.from(notification);
        NotificationMessage message = new NotificationMessage(playerId.value(), "notifications", model);
        template.convertAndSend(RabbitMQTopology.EXCHANGE_USER_SOCKET, "user.message", message);
    }
}