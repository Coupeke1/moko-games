package be.kdg.team22.communicationservice.infrastructure.notification;

import be.kdg.team22.communicationservice.application.queries.NotificationFilter;
import be.kdg.team22.communicationservice.application.queries.PageResult;
import be.kdg.team22.communicationservice.application.queries.Pagination;
import be.kdg.team22.communicationservice.domain.notification.*;
import be.kdg.team22.communicationservice.infrastructure.notification.jpa.NotificationEntity;
import be.kdg.team22.communicationservice.infrastructure.notification.jpa.NotificationJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class DbNotificationRepository implements NotificationRepository {
    private final NotificationJpaRepository repository;

    public DbNotificationRepository(final NotificationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(final Notification notification) {
        NotificationEntity saved = repository.save(NotificationEntity.from(notification));
        return saved.to();
    }

    @Override
    public Optional<Notification> findById(final NotificationId id) {
        return repository.findById(id.value()).map(NotificationEntity::to);
    }

    @Override
    public List<Notification> findByRecipientId(final PlayerId playerId) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(playerId.value()).stream().map(NotificationEntity::to).toList();
    }

    @Override
    public List<Notification> findUnreadByRecipientId(final PlayerId playerId) {
        return repository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(playerId.value()).stream().map(NotificationEntity::to).toList();
    }

    @Override
    public PageResult<Notification> findAllByConstraints(final PlayerId playerId, final NotificationFilter type, final NotificationOrigin origin, final Pagination pagination) {
        Boolean read = type == null ? null : (type == NotificationFilter.READ);

        Pageable pageable = PageRequest.of(pagination.page(), pagination.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<NotificationEntity> page = repository.findAllFiltered(playerId.value(), read, origin, pageable);

        List<Notification> notifications = page.getContent().stream().map(NotificationEntity::to).toList();
        return new PageResult<>(notifications, page.isLast());
    }

    @Override
    public List<Notification> findUnreadSince(final PlayerId recipientId, final Instant since) {
        List<NotificationEntity> notifications = repository.findUnreadSince(recipientId.value(), since);
        return notifications.stream().map(NotificationEntity::to).toList();
    }
}