package be.kdg.team22.communicationservice.infrastructure.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.infrastructure.notification.jpa.NotificationEntity;
import be.kdg.team22.communicationservice.infrastructure.notification.jpa.NotificationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DbNotificationRepositoryTest {

    private NotificationJpaRepository jpa;
    private DbNotificationRepository repository;
    private Notification sample;
    private NotificationEntity entity;

    @BeforeEach
    void setup() {
        jpa = mock(NotificationJpaRepository.class);
        repository = new DbNotificationRepository(jpa);

        sample = new Notification(
                NotificationId.from(UUID.randomUUID()),
                PlayerId.from(UUID.randomUUID()),
                NotificationType.FRIEND_REQUEST_RECEIVED,
                "Title",
                "Body",
                Instant.now(),
                false
        );

        entity = NotificationEntity.from(sample);
    }

    @Test
    void save_shouldConvertDomainToEntityAndBack() {
        // Arrange
        when(jpa.save(any(NotificationEntity.class))).thenReturn(entity);

        // Act
        Notification saved = repository.save(sample);

        verify(jpa).save(any(NotificationEntity.class));
        assertThat(saved.id()).isEqualTo(sample.id());
        assertThat(saved.title()).isEqualTo(sample.title());
    }

    @Test
    void findById_shouldReturnMappedDomainNotification() {
        when(jpa.findById(sample.id().value())).thenReturn(Optional.of(entity));

        Optional<Notification> result = repository.findById(sample.id());

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(sample.id());
    }

    @Test
    void findById_shouldReturnEmptyWhenNotFound() {
        when(jpa.findById(sample.id().value())).thenReturn(Optional.empty());

        Optional<Notification> result = repository.findById(sample.id());

        assertThat(result).isEmpty();
    }

    @Test
    void findByRecipientId_shouldMapEntitiesToDomainObjects() {
        UUID playerId = sample.recipientId().value();

        when(jpa.findByRecipientIdOrderByCreatedAtDesc(playerId))
                .thenReturn(List.of(entity));

        List<Notification> result = repository.findByRecipientId(sample.recipientId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().title()).isEqualTo(sample.title());
    }

    @Test
    void findUnreadByRecipientId_shouldReturnOnlyUnreadMappedNotifications() {
        UUID playerId = sample.recipientId().value();

        when(jpa.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(playerId))
                .thenReturn(List.of(entity));

        List<Notification> result = repository.findUnreadByRecipientId(sample.recipientId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().isRead()).isFalse();
        assertThat(result.getFirst().id()).isEqualTo(sample.id());
    }
}