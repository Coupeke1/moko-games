package be.kdg.team22.socketservice.infrastructure;

import be.kdg.team22.socketservice.config.RabbitMQTopology;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrivateListener {
    private final SimpMessagingTemplate template;

    public PrivateListener(final SimpMessagingTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_USER_SOCKET)
    public void handle(final PrivateMessage message) {
        if (message == null || message.userId() == null || message.queue() == null) return;

        String userId = message.userId().toString();
        String destination = String.format("/queue/%s", message.queue());

        Object outgoing = (message.reason() != null || message.payload() == null)
                ? message
                : message.payload();

        template.convertAndSendToUser(userId, destination, outgoing);
    }
}