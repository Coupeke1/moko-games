package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.social;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.social.FriendRequestAcceptedEvent;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.social.FriendRequestReceivedEvent;
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
class SocialNotificationListenerTest {

    @Mock
    NotificationService service;

    @InjectMocks
    SocialNotificationListener listener;

    @Test
    void handleFriendRequestReceived_shouldSendNotification() {
        UUID senderID = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        FriendRequestReceivedEvent event =
                new FriendRequestReceivedEvent(senderID, "Bob", targetUserId);

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationOrigin.FRIEND_REQUEST_RECEIVED),
                org.mockito.ArgumentMatchers.eq("New friend request"),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value()).isEqualTo(targetUserId);
    }

    @Test
    void handleFriendRequestAccepted_shouldSendNotification() {
        UUID senderId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        FriendRequestAcceptedEvent event =
                new FriendRequestAcceptedEvent(senderId, "Alice", targetUserId);

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationOrigin.FRIEND_REQUEST_ACCEPTED),
                org.mockito.ArgumentMatchers.eq("Friend request accepted"),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value()).isEqualTo(targetUserId);
    }
}