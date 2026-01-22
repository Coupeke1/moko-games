package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import be.kdg.team22.communicationservice.infrastructure.email.EmailService;
import be.kdg.team22.communicationservice.infrastructure.notification.NotificationPublisher;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.NotificationsResponse;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private NotificationRepository repository;
    private NotificationService service;
    private ExternalUserRepository userRepo;
    private EmailService emailService;
    private NotificationPublisher socket;
    private NotificationPublisherService notificationPublisherService;

    private PlayerId recipient;
    private Notification sampleNotification;

    @BeforeEach
    void setup() {
        repository = mock(NotificationRepository.class);
        userRepo = mock(ExternalUserRepository.class);
        emailService = mock(EmailService.class);
        socket = mock(NotificationPublisher.class);
        notificationPublisherService = new NotificationPublisherService(repository, socket);

        service = new NotificationService(repository, notificationPublisherService, userRepo, emailService);

        recipient = PlayerId.from(UUID.randomUUID());

        sampleNotification = new Notification(NotificationId.from(UUID.randomUUID()), recipient, NotificationOrigin.FRIEND_REQUEST_RECEIVED, "Test title", "Test message", Instant.now(), false);
    }

    @Test
    void create_shouldGenerateNotificationAndSaveToRepository() {
        NotificationsResponse prefs = new NotificationsResponse(false, true, true, true, true);
        ProfileResponse profile = new ProfileResponse(recipient.value(), "testuser", "test@example.com", "testImg", "profileDescription");

        when(userRepo.getProfile(recipient.value())).thenReturn(profile);
        when(userRepo.getNotificationsByUser(recipient.value())).thenReturn(prefs);
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification created = service.create(recipient, NotificationOrigin.FRIEND_REQUEST_RECEIVED, "Hello", "Message");

        verify(repository).save(any(Notification.class));
        verify(userRepo).getProfile(recipient.value());
        verify(userRepo).getNotificationsByUser(recipient.value());
        verify(emailService, never()).sendNotificationEmail(anyString(), anyString(), any());

        assertThat(created.id()).isNotNull();
        assertThat(created.recipientId()).isEqualTo(recipient);
        assertThat(created.origin()).isEqualTo(NotificationOrigin.FRIEND_REQUEST_RECEIVED);
        assertThat(created.title()).isEqualTo("Hello");
        assertThat(created.message()).isEqualTo("Message");
    }

    @Test
    void create_shouldSendEmailWhenPreferencesAllow() {
        NotificationsResponse prefs = new NotificationsResponse(true, true, true, true, true);
        ProfileResponse profile = new ProfileResponse(recipient.value(), "testuser", "test@example.com", "testImg", "profileDescription");

        when(userRepo.getProfile(recipient.value())).thenReturn(profile);
        when(userRepo.getNotificationsByUser(recipient.value())).thenReturn(prefs);
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification created = service.create(recipient, NotificationOrigin.FRIEND_REQUEST_RECEIVED, "Hello", "Message");

        ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(emailService).sendNotificationEmail(eq("test@example.com"), eq("testuser"), notifCaptor.capture());

        assertThat(notifCaptor.getValue().origin()).isEqualTo(NotificationOrigin.FRIEND_REQUEST_RECEIVED);
    }

    @Test
    void create_shouldNotSendEmailWhenOriginDisabled() {
        NotificationsResponse prefs = new NotificationsResponse(true, false, true, true, true);
        ProfileResponse profile = new ProfileResponse(recipient.value(), "testuser", "test@example.com", "testImg", "profileDescription");

        when(userRepo.getProfile(recipient.value())).thenReturn(profile);
        when(userRepo.getNotificationsByUser(recipient.value())).thenReturn(prefs);
        when(repository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        service.create(recipient, NotificationOrigin.FRIEND_REQUEST_RECEIVED, "Hello", "Message");

        // Then - geen email verzonden
        verify(emailService, never()).sendNotificationEmail(anyString(), anyString(), any());
    }

    @Test
    void getNotifications_shouldReturnNotificationsFromRepository() {
        when(repository.findByRecipientId(recipient)).thenReturn(List.of(sampleNotification));

        List<Notification> result = service.getNotifications(recipient);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(sampleNotification);
    }

    @Test
    void getUnreadNotifications_shouldReturnOnlyUnread() {
        Notification unread = sampleNotification;
        Notification readNotif = new Notification(NotificationId.from(UUID.randomUUID()), recipient, sampleNotification.origin(), sampleNotification.title(), sampleNotification.message(), sampleNotification.createdAt(), true);

        when(repository.findUnreadByRecipientId(recipient)).thenReturn(List.of(unread));

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

        assertThatThrownBy(() -> service.markAsRead(id)).isInstanceOf(NotificationNotFoundException.class).hasMessageContaining(id.value().toString());
    }
}
