package be.kdg.team22.checkersservice.infrastructure.messaging;

import be.kdg.team22.checkersservice.application.events.GameEventPublisher;
import be.kdg.team22.checkersservice.config.RabbitMQTopology;
import be.kdg.team22.checkersservice.domain.events.*;
import be.kdg.team22.checkersservice.domain.events.exceptions.PublishAchievementException;
import be.kdg.team22.checkersservice.domain.events.exceptions.RabbitNotReachableException;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMqGameplayEventPublisher implements GameEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqGameplayEventPublisher(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishAchievement(final GameAchievementEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_CHECKERS_GAMEPLAY,
                    event,
                    msg -> {
                        msg.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                        msg.getMessageProperties().setCorrelationId(event.gameId().toString());
                        return msg;
                    }
            );
        } catch (AmqpConnectException exception) {
            throw new RabbitNotReachableException();
        } catch (AmqpException exception) {
            throw new PublishAchievementException();
        }
    }
}