package be.kdg.team22.communicationservice.domain.notification;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Notification {
    private final NotificationId id;
    private final PlayerId recipientId;
    private final NotificationType type;
    private final String title;
    private final String message;
    private final Instant createdAt;
    private boolean read;

    public Notification(final NotificationId id,
                        final PlayerId recipientId,
                        final NotificationType type,
                        final String title,
                        final String message,
                        final Instant createdAt,
                        final boolean read) {

        this.id = id;
        this.recipientId = recipientId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public static Notification create(final PlayerId recipientId,
                                      final NotificationType type,
                                      final String title,
                                      final String message) {
        return new Notification(
                NotificationId.newId(),
                recipientId,
                type,
                title,
                message,
                Instant.now(),
                false
        );
    }

    public NotificationId id() {
        return id;
    }

    public PlayerId recipientId() {
        return recipientId;
    }

    public NotificationType type() {
        return type;
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

    public void markAsRead() {
        if (!this.read) {
            this.read = true;
        }
    }
}