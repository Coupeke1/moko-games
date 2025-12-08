package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.UserPreferencesNotFoundException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.UserProfileNotFoundException;
import be.kdg.team22.communicationservice.infrastructure.email.EmailService;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.PreferencesResponse;
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

        Notification saved = repository.save(notification);

        sendEmailIfAllowed(recipient.value(), saved);

        return saved;
    }

    private void sendEmailIfAllowed(UUID recipientId, Notification notification) {
        ProfileResponse profile = userRepository.getProfile(recipientId);
        if (profile == null || profile.email() == null) {
            throw new UserProfileNotFoundException(recipientId);
        }

        PreferencesResponse prefsResponse = userRepository.getPreferencesByUserId(recipientId);
        if (prefsResponse == null) {
            throw new UserPreferencesNotFoundException(recipientId);
        }

        NotificationPreferences prefs = prefsResponse.to();

        if (prefs.allowsEmail(notification)) {
            emailService.sendNotificationEmail(
                                        profile.email(),
                    profile.username(),
                    notification
            );
        }
    }

    public List<Notification> getNotifications(final PlayerId playerId, final String jwtToken) {
        return repository.findByRecipientId(playerId);
    }

    public List<Notification> getUnreadNotifications(final PlayerId playerId, final String jwtToken) {
        return repository.findUnreadByRecipientId(playerId);
    }

    public void markAsRead(final NotificationId id) {
        Notification notification =
                repository.findById(id).orElseThrow(() -> new NotificationNotFoundException(id));

        notification.markAsRead();
        repository.save(notification);
    }
}