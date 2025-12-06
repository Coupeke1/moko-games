package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private NotificationRepository repository;
    private NotificationService service;

    private PlayerId recipient;
    private Notification sampleNotification;

    @BeforeEach
    void setup() {
        repository = mock(NotificationRepository.class);
        service = new NotificationService(repository);

        recipient = PlayerId.from(UUID.randomUUID());

        sampleNotification = new Notification(
                NotificationId.from(UUID.randomUUID()),
                recipient,
                NotificationType.FRIEND_REQUEST_RECEIVED,
                "Test title",
                "Test message",
                Instant.now(),
                false
        );
    }

    @Test
    void create_shouldGenerateNotificationAndSaveToRepository() {
        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Notification created = service.create(
                recipient,
                NotificationType.FRIEND_REQUEST_RECEIVED,
                "Hello",
                "Message"
        );

        verify(repository).save(any(Notification.class));

        assertThat(created.id()).isNotNull();
        assertThat(created.recipientId()).isEqualTo(recipient);
        assertThat(created.type()).isEqualTo(NotificationType.FRIEND_REQUEST_RECEIVED);
        assertThat(created.title()).isEqualTo("Hello");
        assertThat(created.message()).isEqualTo("Message");
    }

    @Test
    void getNotifications_shouldReturnNotificationsFromRepository() {
        when(repository.findByRecipientId(recipient))
                .thenReturn(List.of(sampleNotification));

        List<Notification> result = service.getNotifications(recipient);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(sampleNotification);
    }

    @Test
    void getUnreadNotifications_shouldReturnOnlyUnread() {
        Notification unread = sampleNotification;
        Notification readNotif = new Notification(
                sampleNotification.id(),
                recipient,
                sampleNotification.type(),
                sampleNotification.title(),
                sampleNotification.message(),
                sampleNotification.createdAt(),
                true
        );

        when(repository.findUnreadByRecipientId(recipient))
                .thenReturn(List.of(unread));

        List<Notification> result = service.getUnreadNotifications(recipient);

        assertThat(result).containsExactly(unread);
        assertThat(result).doesNotContain(readNotif);
    }

    @Test
    void markAsRead_shouldMarkNotificationAsReadAndSave() {
        NotificationId id = sampleNotification.id();

        when(repository.findById(id)).thenReturn(Optional.of(sampleNotification));
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        service.markAsRead(id);

        assertThat(sampleNotification.isRead()).isTrue();

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());

        Notification saved = captor.getValue();
        assertThat(saved.isRead()).isTrue();
    }

    @Test
    void markAsRead_shouldThrowIfNotFound() {
        NotificationId id = NotificationId.from(UUID.randomUUID());

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markAsRead(id))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining(id.value().toString());
    }
}