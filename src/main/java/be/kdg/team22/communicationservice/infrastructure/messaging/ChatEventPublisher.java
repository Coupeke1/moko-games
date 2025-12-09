package be.kdg.team22.communicationservice.infrastructure.messaging;

import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.chat.DirectMessageReceivedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public ChatEventPublisher(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishDirectMessage(final UUID senderId,
                                     final String senderName,
                                     final UUID recipientId,
                                     final String messagePreview,
                                     final UUID channelId) {
        DirectMessageReceivedEvent event = new DirectMessageReceivedEvent(
                senderId,
                senderName,
                recipientId,
                messagePreview,
                channelId
        );
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_CHAT,
                "chat.direct-message",
                event
        );
    }
}