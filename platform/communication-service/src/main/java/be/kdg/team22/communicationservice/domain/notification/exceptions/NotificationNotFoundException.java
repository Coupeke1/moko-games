package be.kdg.team22.communicationservice.domain.notification.exceptions;

import be.kdg.team22.communicationservice.domain.notification.NotificationId;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(NotificationId id) {
        super("Notification with id " + id + " not found");
    }
}