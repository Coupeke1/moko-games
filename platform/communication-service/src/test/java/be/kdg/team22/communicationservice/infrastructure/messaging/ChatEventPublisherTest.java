package be.kdg.team22.communicationservice.infrastructure.messaging;

import be.kdg.team22.communicationservice.config.RabbitMQTopology;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.chat.DirectMessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatEventPublisherTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    ChatEventPublisher publisher;

    @Test
    void publishDirectMessage_shouldSendEventToCorrectExchangeAndRoutingKey() {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        String senderName = "TestUser";
        String messagePreview = "Hello friend!";

        publisher.publishDirectMessage(senderId, senderName, recipientId, messagePreview, channelId);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DirectMessageReceivedEvent> eventCaptor = ArgumentCaptor.forClass(DirectMessageReceivedEvent.class);

        verify(rabbitTemplate).convertAndSend(
                exchangeCaptor.capture(),
                routingKeyCaptor.capture(),
                eventCaptor.capture()
        );

        assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQTopology.EXCHANGE_CHAT);
        assertThat(routingKeyCaptor.getValue()).isEqualTo("chat.direct-message");

        DirectMessageReceivedEvent event = eventCaptor.getValue();
        assertThat(event.senderId()).isEqualTo(senderId);
        assertThat(event.senderName()).isEqualTo(senderName);
        assertThat(event.recipientId()).isEqualTo(recipientId);
        assertThat(event.messagePreview()).isEqualTo(messagePreview);
        assertThat(event.channelId()).isEqualTo(channelId);
    }

    @Test
    void publishDirectMessage_shouldTruncateLongMessages() {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        String longMessage = "This is already truncated by ChatService before reaching publisher";

        publisher.publishDirectMessage(senderId, "User", recipientId, longMessage, channelId);

        ArgumentCaptor<DirectMessageReceivedEvent> eventCaptor = ArgumentCaptor.forClass(DirectMessageReceivedEvent.class);
        verify(rabbitTemplate).convertAndSend(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                eventCaptor.capture()
        );

        assertThat(eventCaptor.getValue().messagePreview()).isEqualTo(longMessage);
    }
}
