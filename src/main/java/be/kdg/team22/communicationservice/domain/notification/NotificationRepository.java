package be.kdg.team22.communicationservice.domain.notification;

import be.kdg.team22.communicationservice.application.queries.NotificationReadFilter;
import be.kdg.team22.communicationservice.application.queries.PageResult;
import be.kdg.team22.communicationservice.application.queries.Pagination;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);

    Optional<Notification> findById(NotificationId id);

    List<Notification> findByRecipientId(PlayerId recipientId);

    List<Notification> findUnreadByRecipientId(PlayerId recipientId);

    PageResult<Notification> findAllByConstraints(
            PlayerId playerId,
            NotificationReadFilter type,
            NotificationType origin,
            Pagination pagination
    );
}