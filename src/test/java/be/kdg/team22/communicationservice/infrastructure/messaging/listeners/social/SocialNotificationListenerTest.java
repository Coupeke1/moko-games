package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.social;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.social.FriendRequestAcceptedEvent;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.social.FriendRequestReceivedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        FriendRequestReceivedEvent event =
                new FriendRequestReceivedEvent("73355149-b638-494b-a200-04d0eb5b5032", "Bob", "a2797baf-e115-40de-9b2b-83e4b2633039");

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationType.FRIEND_REQUEST_RECEIVED),
                org.mockito.ArgumentMatchers.eq("New friend request"),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value().toString()).isEqualTo("a2797baf-e115-40de-9b2b-83e4b2633039");
    }

    @Test
    void handleFriendRequestAccepted_shouldSendNotification() {
        FriendRequestAcceptedEvent event =
                new FriendRequestAcceptedEvent("d986aa70-04be-4f8d-af68-f59ced493efa", "Alice", "308339c6-f216-41e3-bdc1-4b52f3c1015e");

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationType.FRIEND_REQUEST_ACCEPTED),
                org.mockito.ArgumentMatchers.eq("Friend request accepted"),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value().toString()).isEqualTo("308339c6-f216-41e3-bdc1-4b52f3c1015e");
    }
}