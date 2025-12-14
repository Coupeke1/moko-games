package be.kdg.team22.communicationservice.domain.notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(NotificationId id);

    List<Notification> findByRecipientId(PlayerId recipientId);

    List<Notification> findUnreadByRecipientId(PlayerId recipientId);

    List<Notification> findByRecipientIdAndType(PlayerId recipientId, NotificationType type);

    List<Notification> findReadByRecipientId(PlayerId playerId);
}