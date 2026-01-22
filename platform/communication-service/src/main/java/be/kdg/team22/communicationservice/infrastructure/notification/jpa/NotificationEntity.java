package be.kdg.team22.communicationservice.infrastructure.notification.jpa;

import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationId;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;
import be.kdg.team22.communicationservice.domain.notification.PlayerId;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationOrigin origin;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean read;

    protected NotificationEntity() {
    }

    public NotificationEntity(final UUID id,
                              final UUID recipientId,
                              final NotificationOrigin origin,
                              final String title,
                              final String message,
                              final Instant createdAt,
                              final boolean read) {
        this.id = id;
        this.recipientId = recipientId;
        this.origin = origin;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public static NotificationEntity from(final Notification notification) {
        return new NotificationEntity(
                notification.id().value(),
                notification.recipientId().value(),
                notification.origin(),
                notification.title(),
                notification.message(),
                notification.createdAt(),
                notification.isRead()
        );
    }

    public Notification to() {
        return new Notification(
                NotificationId.from(id),
                PlayerId.from(recipientId),
                origin,
                title,
                message,
                createdAt,
                read
        );
    }

    public UUID id() {
        return id;
    }

    public UUID recipientId() {
        return recipientId;
    }

    public NotificationOrigin origin() {
        return origin;
    }

    public String title() {
        return title;
    }

    public String message() {
        return message;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }
}