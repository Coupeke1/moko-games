package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.domain.profile.NotificationPreferences;

public record PreferencesModel(
        boolean receiveEmail,
        boolean social,
        boolean achievement,
        boolean commerce,
        boolean chat
) {
    public static PreferencesModel from(NotificationPreferences prefs) {
        return new PreferencesModel(
                prefs.receiveEmail(),
                prefs.social(),
                prefs.achievement(),
                prefs.commerce(),
                prefs.chat()
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