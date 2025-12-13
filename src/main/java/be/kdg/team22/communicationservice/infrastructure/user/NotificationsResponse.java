package be.kdg.team22.communicationservice.infrastructure.user;

import be.kdg.team22.communicationservice.domain.notification.NotificationPreferences;

public record NotificationsResponse(
        boolean receiveEmail,
        boolean social,
        boolean achievement,
        boolean commerce,
        boolean chat
) {
    public static NotificationsResponse defaultPreferences() {
        return new NotificationsResponse(
                false,
                true,
                true,
                true,
                true
        );
    }

    public NotificationPreferences to() {
        return new NotificationPreferences(
                receiveEmail,
                social,
                achievement,
                commerce,
                chat
        );
    }
}