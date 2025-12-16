package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.achievements;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.achievements.AchievementUnlockedEvent;
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
class AchievementNotificationListenerTest {

    @Mock
    NotificationService service;

    @InjectMocks
    AchievementNotificationListener listener;

    @Test
    void handle_shouldCreateAchievementNotification() {
        UUID playerId = UUID.randomUUID();
        UUID achievementId = UUID.randomUUID();
        AchievementUnlockedEvent event =
                new AchievementUnlockedEvent(playerId, achievementId, "Winner", "desc");

        listener.handle(event);

        ArgumentCaptor<PlayerId> playerCaptor = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                playerCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationOrigin.ACHIEVEMENT_UNLOCKED),
                titleCaptor.capture(),
                messageCaptor.capture()
        );

        assertThat(playerCaptor.getValue().value()).isEqualTo(playerId);
        assertThat(titleCaptor.getValue()).isEqualTo("Achievement unlocked");
    }
}