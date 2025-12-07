package be.kdg.team22.communicationservice.infrastructure.user;

import be.kdg.team22.communicationservice.domain.notification.NotificationPreferences;

public record PreferencesResponse(
        boolean receivePlatform,
        boolean receiveEmail,
        boolean social,
        boolean achievement,
        boolean commerce,
        boolean chat
) {
    public static PreferencesResponse defaultPreferences() {
        return new PreferencesResponse(
                true,
                false,
                true,
                true,
                true,
                true
        );
    }

    public NotificationPreferences to() {
        return new NotificationPreferences(
                receivePlatform,
                receiveEmail,
                social,
                achievement,
                commerce,
                chat
        );
    }
}