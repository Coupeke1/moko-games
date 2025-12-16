package be.kdg.team22.communicationservice.infrastructure.notification;

import be.kdg.team22.communicationservice.application.queries.NotificationReadFilter;
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

import java.util.List;
import java.util.Optional;

@Repository
public class DbNotificationRepository implements NotificationRepository {
    private final NotificationJpaRepository jpa;

    public DbNotificationRepository(final NotificationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Notification save(final Notification notification) {
        NotificationEntity saved = jpa.save(NotificationEntity.from(notification));
        return saved.to();
    }

    @Override
    public Optional<Notification> findById(final NotificationId id) {
        return jpa.findById(id.value()).map(NotificationEntity::to);
    }

    @Override
    public List<Notification> findByRecipientId(final PlayerId playerId) {
        return jpa.findByRecipientIdOrderByCreatedAtDesc(playerId.value())
                .stream()
                .map(NotificationEntity::to)
                .toList();
    }

    @Override
    public List<Notification> findUnreadByRecipientId(final PlayerId playerId) {
        return jpa.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(playerId.value())
                .stream()
                .map(NotificationEntity::to)
                .toList();
    }

    @Override
    public PageResult<Notification> findAllByConstraints(
            final PlayerId playerId,
            final NotificationReadFilter type,
            final NotificationOrigin origin,
            final Pagination pagination
    ) {
        Boolean read = null;
        if (type != null) {
            read = (type == NotificationReadFilter.READ);
        }

        Pageable pageable = PageRequest.of(
                pagination.page(),
                pagination.size(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<NotificationEntity> page = jpa.findAllFiltered(playerId.value(), read, origin, pageable);

        List<Notification> items = page.getContent()
                .stream()
                .map(NotificationEntity::to)
                .toList();

        return new PageResult<>(items, page.isLast());
    }
}