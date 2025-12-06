package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.chat;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.chat.DirectMessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        DirectMessageReceivedEvent event =
                new DirectMessageReceivedEvent("73355149-b638-494b-a200-04d0eb5b5032", "Alice", "56d28984-a95c-4033-8512-c9ae12febb3f", "Hi!", "channel-123");

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> message = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationType.DIRECT_MESSAGE),
                title.capture(),
                message.capture()
        );

        assertThat(player.getValue().value().toString()).isEqualTo("56d28984-a95c-4033-8512-c9ae12febb3f");
        assertThat(title.getValue()).isEqualTo("New private message from Alice");
        assertThat(message.getValue()).isEqualTo("Hi!");
    }
}