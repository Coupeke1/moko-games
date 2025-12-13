package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.domain.profile.Notifications;

public record NotificationsModel(
        boolean receiveEmail, boolean social,
        boolean achievements, boolean commerce,
        boolean chat) {
    public static NotificationsModel from(Notifications prefs) {
        return new NotificationsModel(prefs.receiveEmail(), prefs.social(), prefs.achievements(), prefs.commerce(), prefs.chat());
    }

    public Notifications to() {
        return new Notifications(receiveEmail, social, achievements, commerce, chat);
    }
}