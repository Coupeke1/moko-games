package be.kdg.team22.socialservice.infrastructure.messaging;

import be.kdg.team22.socialservice.config.RabbitMQTopology;
import be.kdg.team22.socialservice.infrastructure.messaging.events.FriendRequestAcceptedEvent;
import be.kdg.team22.socialservice.infrastructure.messaging.events.FriendRequestReceivedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SocialEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public SocialEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishFriendRequestReceived(final FriendRequestReceivedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_SOCIAL,
                RabbitMQTopology.ROUTING_KEY_FRIEND_REQUEST_RECEIVED,
                event
        );
    }

    public void publishFriendRequestAccepted(final FriendRequestAcceptedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_SOCIAL,
                RabbitMQTopology.ROUTING_KEY_FRIEND_REQUEST_ACCEPTED,
                event
        );
    }
}

