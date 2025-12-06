package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.store;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.store.OrderCompletedEvent;
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
class StoreNotificationListenerTest {

    @Mock
    NotificationService service;

    @InjectMocks
    StoreNotificationListener listener;

    @Test
    void handle_shouldCreateOrderNotification() {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        OrderCompletedEvent event = new OrderCompletedEvent(userId, orderId, null);

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationType.ORDER_COMPLETED),
                title.capture(),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value()).isEqualTo(userId);
        assertThat(title.getValue()).isEqualTo("Order completed");
    }
}