package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationRepository;
import be.kdg.team22.communicationservice.infrastructure.notification.NotificationSocketPublisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class NotificationPublisherService {
    private final NotificationRepository repository;
    private final NotificationSocketPublisher publisher;

    public NotificationPublisherService(final NotificationRepository repository, final NotificationSocketPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void saveAndPublish(final Notification notification) {
        repository.save(notification);
        publisher.publishToPlayer(notification.recipientId(), notification);
    }
}
