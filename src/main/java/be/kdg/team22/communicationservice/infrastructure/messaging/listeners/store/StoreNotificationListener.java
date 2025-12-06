package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.store;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.store.OrderCompletedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StoreNotificationListener {
    private final NotificationService notifications;

    public StoreNotificationListener(final NotificationService notifications) {
        this.notifications = notifications;
    }

    @RabbitListener(queues = RabbitMQTopology.Q_ORDER_COMPLETED)
    public void handle(final OrderCompletedEvent event) {
        PlayerId recipient = PlayerId.create(event.userId());

        notifications.create(
                recipient,
                NotificationType.ORDER_COMPLETED,
                "Order completed",
                "Your order (" + event.orderId() + ") has successfully been completed."
        );
    }
}