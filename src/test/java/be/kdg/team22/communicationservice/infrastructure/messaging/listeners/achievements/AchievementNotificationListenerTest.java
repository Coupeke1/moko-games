package be.kdg.team22.communicationservice.infrastructure.messaging.listeners.achievements;

import be.kdg.team22.communicationservice.application.notification.NotificationService;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import be.kdg.team22.communicationservice.infrastructure.messaging.events.achievements.AchievementUnlockedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        AchievementUnlockedEvent event =
                new AchievementUnlockedEvent("73355149-b638-494b-a200-04d0eb5b5032", "ach-001", "Winner", "desc");

        listener.handle(event);

        ArgumentCaptor<PlayerId> playerCaptor = ArgumentCaptor.forClass(PlayerId.class);
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(service).create(
                playerCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(NotificationType.ACHIEVEMENT_UNLOCKED),
                titleCaptor.capture(),
                messageCaptor.capture()
        );

        assertThat(playerCaptor.getValue().value().toString()).isEqualTo("73355149-b638-494b-a200-04d0eb5b5032");
        assertThat(titleCaptor.getValue()).isEqualTo("Achievement acquired!");
        assertThat(messageCaptor.getValue()).isEqualTo("You acquired: Winner");
    }
}