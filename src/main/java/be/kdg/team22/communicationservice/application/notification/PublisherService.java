package be.kdg.team22.communicationservice.application.notification;

import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationRepository;
import be.kdg.team22.communicationservice.infrastructure.notification.NotificationPublisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PublisherService {
    private final NotificationRepository repository;
    private final NotificationPublisher publisher;

    public PublisherService(final NotificationRepository repository, final NotificationPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void saveAndPublish(final Notification notification) {
        repository.save(notification);
        publisher.publishToPlayer(notification.recipientId(), notification);
    }
}
