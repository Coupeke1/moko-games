package be.kdg.team22.communicationservice.infrastructure.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.infrastructure.notification.jpa.NotificationEntity;
import be.kdg.team22.communicationservice.infrastructure.notification.jpa.NotificationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DbNotificationRepository implements NotificationRepository {
    private final NotificationJpaRepository jpa;

    public DbNotificationRepository(final NotificationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Notification save(final Notification notification) {
        NotificationEntity saved = jpa.save(NotificationEntity.from(notification));
        return saved.to();
    }

    @Override
    public Optional<Notification> findById(final NotificationId id) {
        return jpa.findById(id.value()).map(NotificationEntity::to);
    }

    @Override
    public List<Notification> findByRecipientId(final PlayerId playerId) {
        return jpa.findByRecipientIdOrderByCreatedAtDesc(playerId.value())
                .stream()
                .map(NotificationEntity::to)
                .toList();
    }

    @Override
    public List<Notification> findUnreadByRecipientId(final PlayerId playerId) {
        return jpa.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(playerId.value())
                .stream()
                .map(NotificationEntity::to)
                .toList();
    }

    @Override
    public List<Notification> findReadByRecipientId(PlayerId playerId) {
        return jpa.findByRecipientIdAndReadTrueOrderByCreatedAtDesc(playerId.value())
                .stream()
                .map(NotificationEntity::to)
                .toList();
    }

    @Override
    public List<Notification> findByRecipientIdAndType(final PlayerId playerId, final NotificationType type) {
        return jpa.findByRecipientIdAndTypeOrderByCreatedAtDesc(playerId.value(), type)
                .stream()
                .map(NotificationEntity::to)
                .toList();
    }
}