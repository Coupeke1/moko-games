package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.PreferencesResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository repository;
    private final ExternalUserRepository userRepository;

    public NotificationService(final NotificationRepository repository,
                               final ExternalUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private NotificationPreferences getPreferences(String jwtToken) {
        PreferencesResponse response = userRepository.getPreferences(jwtToken);
        return response.to();
    }

    public Notification create(final PlayerId recipient,
                               final NotificationType type,
                               final String title,
                               final String message) {

        Notification notification = Notification.create(
                recipient, type, title, message
        );

        return repository.save(notification);
    }

    public List<Notification> getNotifications(final PlayerId playerId, final String jwtToken) {
        NotificationPreferences prefs = getPreferences(jwtToken);

        return repository.findByRecipientId(playerId)
                .stream()
                .filter(prefs::allows)
                .toList();
    }

    public List<Notification> getUnreadNotifications(final PlayerId playerId, final String jwtToken) {
        NotificationPreferences prefs = getPreferences(jwtToken);

        return repository.findUnreadByRecipientId(playerId)
                .stream()
                .filter(prefs::allows)
                .toList();
    }

    public void markAsRead(final NotificationId id) {
        Notification notification =
                repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));

        notification.markAsRead();
        repository.save(notification);
    }
}