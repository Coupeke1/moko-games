package be.kdg.team22.communicationservice.infrastructure.notification.jpa;

import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    List<NotificationEntity> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(UUID recipientId);

    @Query("""
            select n from NotificationEntity n
            where n.recipientId = :recipientId
              and (:read is null or n.read = :read)
              and (:origin is null or n.origin = :origin)
            """)
    Page<NotificationEntity> findAllFiltered(@Param("recipientId") UUID recipientId, @Param("read") Boolean read, @Param("origin") NotificationOrigin origin, Pageable pageable);

    @Query("""
            select n
            from NotificationEntity n
            where n.recipientId = :recipientId
              and n.createdAt >= :since
              and n.read = false
            """)
    List<NotificationEntity> findUnreadSince(UUID recipientId, Instant since);
}