package be.kdg.team22.storeservice.infrastructure.messaging;

import be.kdg.team22.storeservice.config.RabbitMQTopology;
import be.kdg.team22.storeservice.domain.order.events.GamesPurchasedEvent;
import be.kdg.team22.storeservice.domain.order.events.OrderCompletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static be.kdg.team22.storeservice.config.RabbitMQTopology.ROUTING_KEY_GAMES_PURCHASED;
import static be.kdg.team22.storeservice.config.RabbitMQTopology.ROUTING_KEY_ORDER_COMPLETED;

@Component
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCompleted(final OrderCompletedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_STORE,
                ROUTING_KEY_ORDER_COMPLETED,
                event
        );
    }

    public void publishGamesPurchased(final GamesPurchasedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_STORE,
                ROUTING_KEY_GAMES_PURCHASED,
                event
        );
    }
}