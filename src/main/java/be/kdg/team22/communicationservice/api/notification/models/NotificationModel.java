package be.kdg.team22.communicationservice.api.notification.models;

import be.kdg.team22.communicationservice.domain.notification.Notification;
import be.kdg.team22.communicationservice.domain.notification.NotificationOrigin;

import java.time.Instant;
import java.util.UUID;

public record NotificationModel(
        UUID id,
        NotificationOrigin origin,
        String title,
        String message,
        Instant createdAt,
        boolean read
) {
    public static NotificationModel from(final Notification notification) {
        return new NotificationModel(
                notification.id().value(),
                notification.origin(),
                notification.title(),
                notification.message(),
                notification.createdAt(),
                notification.isRead()
        );
    }
}