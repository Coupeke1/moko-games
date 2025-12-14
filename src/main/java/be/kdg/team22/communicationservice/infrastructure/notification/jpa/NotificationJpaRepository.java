package be.kdg.team22.communicationservice.infrastructure.notification.jpa;

import be.kdg.team22.communicationservice.domain.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    List<NotificationEntity> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(UUID recipientId);

    List<NotificationEntity> findByRecipientIdAndReadTrueOrderByCreatedAtDesc(UUID recipientId);

    List<NotificationEntity> findByRecipientIdAndTypeOrderByCreatedAtDesc(UUID recipientId, NotificationType type);
}