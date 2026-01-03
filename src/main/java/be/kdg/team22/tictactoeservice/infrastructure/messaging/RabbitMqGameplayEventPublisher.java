package be.kdg.team22.tictactoeservice.infrastructure.messaging;

import be.kdg.team22.tictactoeservice.application.events.GameEventPublisher;
import be.kdg.team22.tictactoeservice.config.RabbitMQTopology;
import be.kdg.team22.tictactoeservice.domain.events.GameAchievementEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameEndedEvent;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.PublishAchievementException;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.RabbitNotReachableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMqGameplayEventPublisher implements GameEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqGameplayEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqGameplayEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishAchievement(final GameAchievementEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_TICTACTOE_GAMEPLAY,
                    event,
                    msg -> {
                        msg.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                        msg.getMessageProperties().setCorrelationId(event.gameId().toString());
                        return msg;
                    }
            );
            logger.info("Published achievement event for {} game to player {}: {}",
                    event.gameName(), event.playerId(), event.achievementCode()
            );
        } catch (AmqpConnectException exception) {
            throw new RabbitNotReachableException();
        } catch (AmqpException exception) {
            throw new PublishAchievementException();
        }
    }

    @Override
    public void publishGameEnded(final GameEndedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_GAME_ENDED,
                    event,
                    msg -> {
                        msg.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                        msg.getMessageProperties().setCorrelationId(event.instanceId().toString());
                        return msg;
                    }
            );
            logger.info("Published game ended event for {} game with instance ID {}",
                    "tic-tac-toe", event.instanceId()
            );
        } catch (AmqpConnectException exception) {
            throw new RabbitNotReachableException();
        } catch (AmqpException exception) {
            throw new PublishAchievementException();
        }
    }
}