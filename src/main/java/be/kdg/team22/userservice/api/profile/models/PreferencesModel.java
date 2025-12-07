package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.domain.profile.NotificationPreferences;

public record PreferencesModel(
        boolean receivePlatform,
        boolean receiveEmail,
        boolean social,
        boolean game,
        boolean achievement,
        boolean commerce,
        boolean chat
) {
    public static PreferencesModel from(NotificationPreferences prefs) {
        return new PreferencesModel(
                prefs.receivePlatform(),
                prefs.receiveEmail(),
                prefs.social(),
                prefs.game(),
                prefs.achievement(),
                prefs.commerce(),
                prefs.chat()
        );
    }

    public NotificationPreferences to() {
        return new NotificationPreferences(
                receivePlatform,
                receiveEmail,
                social,
                game,
                achievement,
                commerce,
                chat
        );
    }
}