package be.kdg.team22.communicationservice.infrastructure.notification.jpa;

import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationEntityTest {

    @Test
    void fromDomain_shouldMapAllFieldsToEntity() {
        UUID id = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        Instant created = Instant.now();

        Notification domain = new Notification(
                NotificationId.from(id),
                PlayerId.from(playerId),
                NotificationType.LOBBY_INVITE,
                "Hello",
                "You are invited",
                created,
                false
        );

        NotificationEntity entity = NotificationEntity.from(domain);

        assertThat(entity.id()).isEqualTo(id);
        assertThat(entity.recipientId()).isEqualTo(playerId);
        assertThat(entity.type()).isEqualTo(NotificationType.LOBBY_INVITE);
        assertThat(entity.title()).isEqualTo("Hello");
        assertThat(entity.message()).isEqualTo("You are invited");
        assertThat(entity.createdAt()).isEqualTo(created);
        assertThat(entity.isRead()).isFalse();
    }

    @Test
    void toDomain_shouldMapAllFieldsFromEntity() {
        UUID id = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        Instant created = Instant.now();

        NotificationEntity entity = new NotificationEntity(
                id,
                playerId,
                NotificationType.ACHIEVEMENT_UNLOCKED,
                "Achieved!",
                "You did something great",
                created,
                true
        );

        Notification domain = entity.to();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.recipientId().value()).isEqualTo(playerId);
        assertThat(domain.type()).isEqualTo(NotificationType.ACHIEVEMENT_UNLOCKED);
        assertThat(domain.title()).isEqualTo("Achieved!");
        assertThat(domain.message()).isEqualTo("You did something great");
        assertThat(domain.createdAt()).isEqualTo(created);
        assertThat(domain.isRead()).isTrue();
    }

    @Test
    void domainToEntityToDomain_roundTripShouldPreserveValues() {
        // Arrange
        Notification original = new Notification(
                NotificationId.from(UUID.randomUUID()),
                PlayerId.from(UUID.randomUUID()),
                NotificationType.DIRECT_MESSAGE,
                "Test",
                "Body",
                Instant.now(),
                false
        );

        NotificationEntity entity = NotificationEntity.from(original);
        Notification result = entity.to();

        assertThat(result.id()).isEqualTo(original.id());
        assertThat(result.recipientId()).isEqualTo(original.recipientId());
        assertThat(result.type()).isEqualTo(original.type());
        assertThat(result.title()).isEqualTo(original.title());
        assertThat(result.message()).isEqualTo(original.message());
        assertThat(result.createdAt()).isEqualTo(original.createdAt());
        assertThat(result.isRead()).isEqualTo(original.isRead());
    }
}