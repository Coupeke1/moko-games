package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import be.kdg.team22.communicationservice.infrastructure.email.EmailService;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.PreferencesResponse;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

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

        // Try to send email notification
        sendEmailIfAllowed(recipient.value(), saved);

        return saved;
    }

    private void sendEmailIfAllowed(UUID recipientId, Notification notification) {
        try {
            // Fetch user profile to get email and preferences
            ProfileResponse profile = userRepository.getProfile(recipientId);
            if (profile == null || profile.email() == null) {
                log.debug("No profile or email found for user {}", recipientId);
                return;
            }

            // Fetch preferences (using a service account or default preferences)
            // Since we don't have JWT here, we'll need to modify this approach
            // For now, we'll create a method that fetches preferences by userId
            PreferencesResponse prefsResponse = userRepository.getPreferencesByUserId(recipientId);
            NotificationPreferences prefs = prefsResponse.to();

            // Check if email is allowed for this notification
            if (prefs.allowsEmail(notification)) {
                emailService.sendNotificationEmail(
                        profile.email(),
                        profile.username(),
                        notification
                );
                log.info("Email notification queued for user {} ({})", profile.username(), profile.email());
            } else {
                log.debug("Email notification not sent to user {} - disabled in preferences", recipientId);
            }

        } catch (Exception e) {
            // Don't fail notification creation if email fails
            log.error("Failed to send email notification for user {}: {}", recipientId, e.getMessage());
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