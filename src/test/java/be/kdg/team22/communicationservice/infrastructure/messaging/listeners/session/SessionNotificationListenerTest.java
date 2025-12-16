package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.session;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.session.LobbyInviteEvent;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.session.PlayerJoinedLobbyEvent;
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
class SessionNotificationListenerTest {

    @Mock
    NotificationService service;

    @InjectMocks
    SessionNotificationListener listener;

    @Test
    void handleLobbyInvite_shouldSendInviteNotification() {
        UUID lobbyId = UUID.randomUUID();
        UUID inviterId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        LobbyInviteEvent event = new LobbyInviteEvent(
                lobbyId, inviterId, "Bob", targetUserId, "TicTacToe"
        );

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationOrigin.LOBBY_INVITE),
                title.capture(),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value()).isEqualTo(targetUserId);
        assertThat(title.getValue()).isEqualTo("Invited for lobby");
    }

    @Test
    void handlePlayerJoined_shouldSendHostNotification() {
        UUID lobbyId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        UUID hostUserId = UUID.randomUUID();
        PlayerJoinedLobbyEvent event = new PlayerJoinedLobbyEvent(
                lobbyId, playerId, "Alice", hostUserId
        );

        listener.handle(event);

        ArgumentCaptor<PlayerId> player = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                player.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationOrigin.PLAYER_JOINED_LOBBY),
                title.capture(),
                org.mockito.ArgumentMatchers.anyString()
        );

        assertThat(player.getValue().value()).isEqualTo(hostUserId);
        assertThat(title.getValue()).isEqualTo("New player joined");
    }
}