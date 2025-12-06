package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.session;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.session.LobbyInviteEvent;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.session.PlayerJoinedLobbyEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SessionNotificationListenerTest {

    @Mock
    NotificationService service;

    @InjectMocks
    SessionNotificationListener listener;

    @Test
    void handleLobbyInvite_shouldSendInviteNotification() {
        LobbyInviteEvent event = new LobbyInviteEvent(
                "b579d557-8f54-4d4f-a320-4ee9386ca285", "34f05ef6-cf82-440c-aa93-89fd74d9a507", "Bob", "4d87e95d-262d-4247-9f58-b7f03957f81b", "TicTacToe"
        );

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationType.LOBBY_INVITE),
                title.capture(),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value().toString()).isEqualTo("4d87e95d-262d-4247-9f58-b7f03957f81b");
        assertThat(title.getValue()).isEqualTo("Invited for lobby");
    }

    @Test
    void handlePlayerJoined_shouldSendHostNotification() {
        PlayerJoinedLobbyEvent event = new PlayerJoinedLobbyEvent(
                "a462d94b-87c4-4c52-80d1-9781bad02c5d", "73355149-b638-494b-a200-04d0eb5b5032", "Alice", "a2797baf-e115-40de-9b2b-83e4b2633039"
        );

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationType.PLAYER_JOINED_LOBBY),
                title.capture(),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value().toString()).isEqualTo("a2797baf-e115-40de-9b2b-83e4b2633039");
        assertThat(title.getValue()).isEqualTo("New player joined");
    }
}