package be.kdg.team22.checkersservice.infrastructure.messaging;

import be.kdg.team22.checkersservice.application.events.GameEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqGameplayEventPublisher implements GameEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqGameplayEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


}