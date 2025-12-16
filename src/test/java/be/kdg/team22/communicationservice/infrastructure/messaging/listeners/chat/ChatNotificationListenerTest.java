package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.chat;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.chat.DirectMessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatNotificationListenerTest {

    @Mock
    NotificationService service;

    @InjectMocks
    ChatNotificationListener listener;

    @Test
    void handle_shouldCreateDirectMessageNotification() {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID channelID = UUID.randomUUID();
        DirectMessageReceivedEvent event =
                new DirectMessageReceivedEvent(senderId, "Alice", recipientId, "Hi!", channelID);

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationOrigin.DIRECT_MESSAGE),
                title.capture(),
                message.capture()
        );

        assertThat(player.getValue().value()).isEqualTo(recipientId);
        assertThat(title.getValue()).isEqualTo("New private message from Alice");
        assertThat(message.getValue()).isEqualTo("Hi!");
    }
}