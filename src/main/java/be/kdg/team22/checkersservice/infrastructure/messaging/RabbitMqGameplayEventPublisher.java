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
    public void publishGameDraw(final GameDrawEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_CHECKERS_DRAW,
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

    @Override
    public void publishGameLost(final GameLostEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_CHECKERS_LOST,
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

    @Override
    public void publishGameWon(final GameWonEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_CHECKERS_WON,
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

    @Override
    public void publishKingPromotionEvent(final KingPromotionEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_CHECKERS_PROMOTION,
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

    @Override
    public void publishMultiCaptureEvent(final MultiCaptureEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_CHECKERS_MULTICAPTURE,
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

    @Override
    public void publishThreeKingsEvent(final ThreeKingsEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQTopology.EXCHANGE_GAMEPLAY,
                    RabbitMQTopology.ROUTING_CHECKERS_THREEKINGS,
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