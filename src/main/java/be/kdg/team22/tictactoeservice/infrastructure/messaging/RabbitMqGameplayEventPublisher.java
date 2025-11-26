package be.kdg.team22.tictactoeservice.infrastructure.messaging;

import be.kdg.team22.tictactoeservice.application.events.GameEventPublisher;
import be.kdg.team22.tictactoeservice.config.RabbitMQTopology;
import be.kdg.team22.tictactoeservice.domain.events.GameDrawEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameWonEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMqGameplayEventPublisher implements GameEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqGameplayEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishGameWon(GameWonEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_GAMEPLAY,
                RabbitMQTopology.ROUTING_TICTACTOE_WON,
                event,
                msg -> {
                    msg.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                    msg.getMessageProperties().setCorrelationId(event.gameId().toString());
                    return msg;
                }
        );
    }

    @Override
    public void publishGameDraw(GameDrawEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQTopology.EXCHANGE_GAMEPLAY,
                RabbitMQTopology.ROUTING_TICTACTOE_DRAW,
                event,
                msg -> {
                    msg.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                    msg.getMessageProperties().setCorrelationId(event.gameId().toString());
                    return msg;
                }
        );
    }
}