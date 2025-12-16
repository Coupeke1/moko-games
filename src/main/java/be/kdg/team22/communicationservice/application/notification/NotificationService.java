package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.application.queries.NotificationReadFilter;
import be.kdg.team22.communicationservice.application.queries.PageResult;
import be.kdg.team22.communicationservice.application.queries.Pagination;
import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import be.kdg.team22.communicationservice.infrastructure.email.EmailService;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.NotificationsResponse;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository repository;
    private final ExternalUserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(final NotificationRepository repository,
                               final ExternalUserRepository userRepository,
                               final EmailService emailService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Notification create(final PlayerId recipient,
                               final NotificationOrigin origin,
                               final String title,
                               final String message) {

        Notification notification = Notification.create(
                recipient, origin, title, message
        );

        Notification saved = repository.save(notification);

        sendEmailIfAllowed(recipient.value(), saved);

        return saved;
    }

    private void sendEmailIfAllowed(UUID recipientId, Notification notification) {
        ProfileResponse profile = userRepository.getProfile(recipientId);

        NotificationsResponse prefsResponse = userRepository.getNotificationsByUser(recipientId);
        NotificationPreferences prefs = prefsResponse.to();

        if (prefs.allowsEmail(notification)) {
            emailService.sendNotificationEmail(
                    profile.email(),
                    profile.username(),
                    notification
            );
        }
    }

    public PageResult<Notification> findAllByConstraints(
            final PlayerId playerId,
            final NotificationReadFilter type,
            final NotificationOrigin origin,
            final Pagination pagination
    ) {
        return repository.findAllByConstraints(playerId, type, origin, pagination);
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