package be.kdg.team22.socketservice.infrastructure;

import be.kdg.team22.socketservice.config.RabbitMQTopology;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserSocketListener {
    private final SimpMessagingTemplate template;

    public UserSocketListener(final SimpMessagingTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = RabbitMQTopology.QUEUE_USER_SOCKET)
    public void handle(final PrivateSocketMessage message) {
        String userId = message.userId().toString();
        String queue = String.format("/queue/%s", message.queue());
        template.convertAndSendToUser(userId, queue, message.payload());
    }
}
