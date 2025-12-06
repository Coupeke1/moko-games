package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(final NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification create(final PlayerId recipient,
                               final NotificationType type,
                               final String title,
                               final String message) {

        Notification notification = Notification.create(
                recipient,
                type,
                title,
                message
        );

        return repository.save(notification);
    }

    public List<Notification> getNotifications(final PlayerId playerId) {
        return repository.findByRecipientId(playerId);
    }

    public List<Notification> getUnreadNotifications(final PlayerId playerId) {
        return repository.findUnreadByRecipientId(playerId);
    }

    public void markAsRead(final NotificationId id) {
        Notification notification =
                repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));

        notification.markAsRead();
        repository.save(notification);
    }
}