package be.kdg.team22.tictactoeservice.infrastructure.messaging;

import be.kdg.team22.tictactoeservice.config.RabbitMQTopology;
import be.kdg.team22.tictactoeservice.domain.events.GameEndedEvent;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.PublishAchievementException;
import be.kdg.team22.tictactoeservice.domain.events.exceptions.RabbitNotReachableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RabbitMqGameplayEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMqGameplayEventPublisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        publisher = new RabbitMqGameplayEventPublisher(rabbitTemplate);
    }

    @Test
    void publishGameEnded_shouldSendToCorrectExchangeAndRoutingKey() {
        UUID instanceId = UUID.randomUUID();
        Instant occurredAt = Instant.now();
        GameEndedEvent event = new GameEndedEvent(instanceId, occurredAt);

        publisher.publishGameEnded(event);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQTopology.EXCHANGE_GAMEPLAY),
                eq(RabbitMQTopology.ROUTING_GAME_ENDED),
                eq(event),
                any(MessagePostProcessor.class)
        );
    }

    @Test
    void publishGameEnded_shouldThrowRabbitNotReachableException_whenConnectionFails() {
        UUID instanceId = UUID.randomUUID();
        GameEndedEvent event = new GameEndedEvent(instanceId, Instant.now());

        doThrow(new AmqpConnectException(new RuntimeException("Connection failed")))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(GameEndedEvent.class), any(MessagePostProcessor.class));

        assertThrows(RabbitNotReachableException.class, () -> publisher.publishGameEnded(event));
    }

    @Test
    void publishGameEnded_shouldThrowPublishAchievementException_whenAmqpExceptionOccurs() {
        UUID instanceId = UUID.randomUUID();
        GameEndedEvent event = new GameEndedEvent(instanceId, Instant.now());

        doThrow(new AmqpException("AMQP error"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(GameEndedEvent.class), any(MessagePostProcessor.class));

        assertThrows(PublishAchievementException.class, () -> publisher.publishGameEnded(event));
    }
}